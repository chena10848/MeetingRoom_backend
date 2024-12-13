package com.example.demo.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.Entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long>  {

    @Query("SELECT " +
            "mtr.id, " +
            "mtrn.id, " +
            "mtrn.meetingroomName, " +
            "u.userName, " +
            "mtr.lesseeStartTime, " +
            "mtr.lesseeEndTime " +
            "FROM MeetingRoom mtr " +
            "INNER JOIN " +
            "MeetingRoomName mtrn " +
            "ON mtr.meetingRoomNameId = mtrn.id " +
            "INNER JOIN " +
            "User u " +
            "ON mtr.lesseeUserId = u.id "
//            "WHERE mtr.lesseeStartTime BETWEEN :startTime AND :endTime " +
//            "AND mtr.lesseeEndTime BETWEEN :startTime AND :endTime"
    )
    List<Object[]> findMeetingRoomsByTimeRange();


    @Transactional
    @Modifying
    @Query("UPDATE MeetingRoom mr SET " +
            "mr.meetingRoomNameId = CASE WHEN :meetingroomNameId IS NOT NULL THEN :meetingroomNameId ELSE mr.meetingRoomNameId END, " +
            "mr.lesseeStartTime = CASE WHEN :startTime IS NOT NULL THEN :startTime ELSE mr.lesseeStartTime END, " +
            "mr.lesseeEndTime = CASE WHEN :endTime IS NOT NULL THEN :endTime ELSE mr.lesseeEndTime END " +
            "WHERE mr.id = :meetingroomId")
    void updateMeetingGroup(@Param("meetingroomNameId") Integer meetingroomNameId,
                    @Param("startTime") LocalDateTime startTime,
                    @Param("endTime") LocalDateTime endTime,
                    @Param("meetingroomId") Integer meetingroomId);


    @Transactional
    @Modifying
    @Query("DELETE FROM MeetingRoom mr WHERE mr.id = :Id")
    void deleteMeetingRoomById(@Param("Id") Integer Id);
}
