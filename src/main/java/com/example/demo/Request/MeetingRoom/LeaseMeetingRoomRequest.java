package com.example.demo.Request.MeetingRoom;

import java.time.LocalDateTime;

public class LeaseMeetingRoomRequest {
    private Integer lesseeUserId;
    private Integer lesseeRoomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer[]  participants;

    public Integer getLesseeUserId() {
        return lesseeUserId;
    }

    public Integer getLesseeRoomId() {
        return lesseeRoomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Integer[] getParticipants() {
        return  participants;
    }

    public void setLesseeUserId(Integer lesseeUserId) {
        this.lesseeUserId = lesseeUserId;
    }

    public void setLesseeRoomId(Integer lesseeRoomId) {
        this.lesseeRoomId = lesseeRoomId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setParticipants(Integer[] participants) {
        this.participants = participants;
    }
}
