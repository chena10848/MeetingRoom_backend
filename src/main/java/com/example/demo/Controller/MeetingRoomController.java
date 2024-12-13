package com.example.demo.Controller;

import com.example.demo.Request.MeetingRoom.DeleteMeetingRoomRequest;
import com.example.demo.Request.MeetingRoom.LeaseMeetingRoomRequest;
import com.example.demo.Request.MeetingRoom.UpdateMeetingRoomRequest;

import com.example.demo.Service.ApiResponse;
import com.example.demo.Service.MeetingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.demo.Component.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Meeting Room API")
public class MeetingRoomController {

    @Autowired
    private final MeetingRoomService meetingRoomService;
    @Autowired
    private final JwtUtil jwtUtil;

    public MeetingRoomController(MeetingRoomService meetingRoomService,JwtUtil jwtUtil) {
        this.meetingRoomService = meetingRoomService;
        this.jwtUtil = jwtUtil;
    }

    //取得所有的使用者
    @GetMapping("/api/allUser")
    @Operation(summary = "取得所有使用者")
    public ResponseEntity<List<Map<String, Object>>> allUser(@RequestHeader("Authorization") String token) {
        this.jwtUtil.validateToken(token);
        return this.meetingRoomService.getAllUser(token);
    }

    @GetMapping("/api/getMeetingRoomId")
    @Operation(summary = "取得所有會議室")
    public ResponseEntity<List<Map<String, Object>>> getMeetingRoomId(@RequestHeader("Authorization") String token) {
        this.jwtUtil.validateToken(token);
        return this.meetingRoomService.getMeetingRoomIdInit();
    }

    //取得一天、七天、三十天預約會議記錄
    @GetMapping("/api/getMeetingRoomList")
    @Operation(summary = "取得預約的會議紀錄")
    public ResponseEntity<List<Map<String, Object>>> getMeetingRoomList(
            @RequestHeader("Authorization") String token,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.jwtUtil.validateToken(token);
        return this.meetingRoomService.getMeetingRoomListInit(startTime, endTime);
    }

    //租借會議室
    @PostMapping("/api/leaseMeetingRoom")
    @Operation(summary = "租借會議室")
    public ApiResponse<String> leaseMeetingRoom(
            @RequestHeader("Authorization") String token,
            @RequestBody LeaseMeetingRoomRequest request
    ){
        this.jwtUtil.validateToken(token);
        return this.meetingRoomService.leaseMeetingRoomInit(request, token);
    }

    //更新會議室
    @PutMapping("/api/updateMeetingRoom")
    @Operation(summary = "更新會議室")
    public ApiResponse<String> updateMeetingRoom(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateMeetingRoomRequest request
    ) {
        this.jwtUtil.validateToken(token);
        return this.meetingRoomService.updateMeetingRoom(request);
    }

    //刪除會議室
    @DeleteMapping("/api/deleteMeetingRoom")
    @Operation(summary = "刪除會議室")
    public ApiResponse<String> deleteMeetingRoom(
            @RequestHeader("Authorization") String token,
            @RequestBody DeleteMeetingRoomRequest request
    ) {
        this.jwtUtil.validateToken(token);
        return this.meetingRoomService.deleteMeetingRoomInit(request);
    }

}
