package com.example.demo.Service;

import com.example.demo.Component.JwtUtil;
import com.example.demo.Entity.MeetingGroupUser;
import com.example.demo.Entity.MeetingRoom;
import com.example.demo.Repository.MeetingGroupUserRepository;
import com.example.demo.Repository.MeetingRoomNameRepository;
import com.example.demo.Repository.MeetingRoomRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Request.MeetingRoom.DeleteMeetingRoomRequest;
import com.example.demo.Request.MeetingRoom.LeaseMeetingRoomRequest;
import com.example.demo.Request.MeetingRoom.UpdateMeetingRoomRequest;
import com.example.demo.dto.EmailMessage;
import com.example.demo.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MeetingRoomService {

    private static final Logger log = LoggerFactory.getLogger(MeetingRoomService.class);

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final MeetingRoomRepository meetingRoomRepository;
    @Autowired
    private final MeetingGroupUserRepository meetingGroupUserRepository;
    @Autowired
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;
    @Autowired
    private final MeetingRoomNameRepository meetingRoomNameRepoistory;
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final RedisTemplate<String, Object> myRedisTemplate; // 注入 RedisTemplate
    private static final String REDIS_SECRET_KEY = "meetingroom:key"; // Redis 中存儲密鑰的 Key
    private static final long REDIS_EXPIRATION = 1; // 密鑰的存儲時間（天）

    @Autowired
    public MeetingRoomService(
            UserRepository userRepository,
            MeetingRoomRepository meetingRoomRepository,
            MeetingGroupUserRepository meetingGroupUserRepository,
            MeetingRoomNameRepository meetingRoomNameRepoistory,
            KafkaTemplate<String, EmailMessage> kafkaTemplate,
            JwtUtil jwtUtil,
            RedisTemplate<String, Object> myRedisTemplate
    ) {
        this.userRepository = userRepository;
        this.meetingRoomRepository = meetingRoomRepository;
        this.meetingGroupUserRepository = meetingGroupUserRepository;
        this.meetingRoomNameRepoistory = meetingRoomNameRepoistory;
        this.kafkaTemplate = kafkaTemplate;
        this.jwtUtil = jwtUtil;
        this.myRedisTemplate = myRedisTemplate;
    }

    public ResponseEntity<List<Map<String, Object>>> getAllUser(String token) {
        try {
            Integer userId = this.jwtUtil.extractUserInfo(token);

            List<Object[]> userInformation = this.userRepository.getUserInformation(userId);

            List<Map<String, Object>> response = userInformation.stream()
                    .map(record -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ID", record[0]);
                        map.put("UserName", record[1]);
                        map.put("Email", record[2]);
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    public ResponseEntity<List<Map<String, Object>>> getMeetingRoomIdInit() {
        try {
            List<Object[]> meetingRoomlist = this.meetingRoomNameRepoistory.getMeetingRoomId();

            List<Map<String, Object>> response = meetingRoomlist.stream()
                    .map(record -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("ID", record[0]);
                        map.put("RoomName", record[1]);
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    //取得會議室
    public ResponseEntity<List<Map<String, Object>>> getMeetingRoomListInit() {
        try {

            List<Map<String, Object>> cachedResponse =
                    (List<Map<String, Object>>) myRedisTemplate.opsForValue().get(REDIS_SECRET_KEY);

            if (cachedResponse != null) {
                log.info("Retrieved meeting room data from Redis cache");
                return ResponseEntity.ok(cachedResponse);
            } else {
                log.info("No cached meeting room data found in Redis");
                List<Object[]> meetingRoomList = this.meetingRoomRepository.findMeetingRoomsByTimeRange();
                List<Map<String, Object>> response = tidyMeetingRoom(meetingRoomList);
                myRedisTemplate.opsForValue().set(REDIS_SECRET_KEY, response, REDIS_EXPIRATION, TimeUnit.DAYS);
                return ResponseEntity.ok(response);
            }
        } catch (Exception ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    /**
     * 整理會議室資料
     *
     * @param meetingRoomList List 會議室資訊
     * @return List
     */
    private List<Map<String, Object>> tidyMeetingRoom(List<Object[]> meetingRoomList) {
        return meetingRoomList.stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ID", record[0]);
                    map.put("MeetingRoomNameId", record[1]);
                    map.put("MeetRoomName", record[2]);
                    map.put("UserName", record[3]);
                    map.put("StartTime", record[4]);
                    map.put("EndTime", record[5]);

                    Integer meetingroomId = (Integer) record[0];
                    List<Object[]> meetingGroupUserId = this.meetingGroupUserRepository.getMeetingGroupUserId(meetingroomId);

                    List<Map<String, Object>> userList = meetingGroupUserId.stream()
                            .map(records -> {
                                Map<String, Object> maps = new HashMap<>();
                                maps.put("UserId", records[0]);
                                maps.put("UserName", records[1]);

                                return maps;
                            })
                            .collect(Collectors.toList());

                    map.put("UserList", userList);

                    return map;
                })
                .collect(Collectors.toList());
    }

    //租借會議
    @Transactional
    public ApiResponse<String> leaseMeetingRoomInit(LeaseMeetingRoomRequest request, String token) {
        try {
            Integer lesseeUserId = this.jwtUtil.extractUserInfo(token);
            Integer lesseeRoomId = request.getLesseeRoomId();
            LocalDateTime startTime = request.getStartTime();
            LocalDateTime endTime = request.getEndTime();
            Integer[] participants = request.getParticipants();

            //寫入 meeting_room
            MeetingRoom meetingRoom = new MeetingRoom();
            meetingRoom.setMeetingRoomNameId(lesseeRoomId);
            meetingRoom.setLesseeStartTime(startTime);
            meetingRoom.setLesseeEndTime(endTime);
            meetingRoom.setLesseeUserId(lesseeUserId);

            MeetingRoom savedRoom = this.meetingRoomRepository.save(meetingRoom);
            Integer meetingRoomId = savedRoom.getId();
            //寫入 meeting_group_user
            if (participants != null) {
                for (Integer item : participants) {
                    MeetingGroupUser meetingGroupUser = new MeetingGroupUser();
                    meetingGroupUser.setMeetingroomId(meetingRoomId);
                    meetingGroupUser.setUserId(item);

                    MeetingGroupUser saveGroup = this.meetingGroupUserRepository.save(meetingGroupUser);
                }

                //信件通知
                List<Object[]> userIds = this.userRepository.getUserEmail(participants);

                userIds.stream()
                        .forEach(record -> {

                            String meetingroomName = this.meetingRoomNameRepoistory.getMeetingRoomName(lesseeRoomId);
                            String email = (String) record[0];
                            String subject = "會議通知！";
                            String text = String.format(
                                    "您已被邀請參加會議\n會議室: %s\n開始時間: %s\n結束時間: %s",
                                    meetingroomName,
                                    startTime.toString(),
                                    endTime.toString()
                            );

                            EmailMessage em = new EmailMessage(email, subject, text);
                            //sendToKafka(em);
                        });
            }

            List<Object[]> meetingRoomList = this.meetingRoomRepository.findMeetingRoomsByTimeRange();
            List<Map<String, Object>> response = tidyMeetingRoom(meetingRoomList);
            myRedisTemplate.opsForValue().set(REDIS_SECRET_KEY, response, REDIS_EXPIRATION, TimeUnit.DAYS);

            return new ApiResponse<>(
                    "Successful",
                    200,
                    ""
            );
        } catch (Exception ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    /**
     * 將寄信功能寫入對列
     *
     * @param em EmailMessage
     */
    @Async
    public void sendToKafka(EmailMessage em) {
        kafkaTemplate.send("email-topic", em);
    }

    //更新會議室
    @Transactional
    public ApiResponse<String> updateMeetingRoom(UpdateMeetingRoomRequest request) {
        try {
            Integer meetingroomId = request.getMeetingroomId();
            Integer meetingroomNameId = request.getMeetingRoomNameId();
            LocalDateTime startTime = request.getStartTime();
            LocalDateTime endTime = request.getEndTime();
            Integer[] createUserIds = request.getCreateUserId();
            Integer[] deleteUserIds = request.getDeleteUserId();

            //更新 Meeting Room 資料
            if (meetingroomNameId != null || startTime != null || endTime != null) {
                this.meetingRoomRepository.updateMeetingGroup(meetingroomNameId, startTime, endTime, meetingroomId);
            }
            //新增 Meeting Group 資料
            if (createUserIds != null) {
                for (Integer userId : createUserIds) {
                    MeetingGroupUser meetingGroupUser = new MeetingGroupUser();
                    meetingGroupUser.setMeetingroomId(meetingroomId);
                    meetingGroupUser.setUserId(userId);

                    this.meetingGroupUserRepository.save(meetingGroupUser);
                }
            }
            //刪除 Meeting Group 資料
            if (deleteUserIds != null) {
                for (Integer userId : deleteUserIds) {
                    this.meetingGroupUserRepository.deleteMeetingGroup(meetingroomId, userId);
                }
            }

            List<Object[]> meetingRoomList = this.meetingRoomRepository.findMeetingRoomsByTimeRange();
            List<Map<String, Object>> response = tidyMeetingRoom(meetingRoomList);
            myRedisTemplate.opsForValue().set(REDIS_SECRET_KEY, response, REDIS_EXPIRATION, TimeUnit.DAYS);

            return new ApiResponse<>(
                    "Successful",
                    200,
                    ""
            );
        } catch (Exception ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    //刪除會議室
    @Transactional
    public ApiResponse<String> deleteMeetingRoomInit(DeleteMeetingRoomRequest request) {
        try {
            Integer Id = request.getMeetingRoomId();

            this.meetingGroupUserRepository.deleteMeetingGroupUsersByRoomId(Id);
            this.meetingRoomRepository.deleteMeetingRoomById(Id);

            List<Object[]> meetingRoomList = this.meetingRoomRepository.findMeetingRoomsByTimeRange();
            List<Map<String, Object>> response = tidyMeetingRoom(meetingRoomList);
            myRedisTemplate.opsForValue().set(REDIS_SECRET_KEY, response, REDIS_EXPIRATION, TimeUnit.DAYS);

            return new ApiResponse<>("Successful", 200, "");
        } catch (Exception ex) {
            throw new UserNotFoundException("刪除會議室失敗: " + ex.getMessage());
        }
    }
}
