package com.example.demo.Request.MeetingRoom;

import org.apache.catalina.util.Introspection;

import java.time.LocalDateTime;

public class UpdateMeetingRoomRequest {

    private Integer meetingroomId;//會議ID
    private Integer meetingroomNameId; //會議室名稱ID
    private LocalDateTime startTime;//開始時間
    private LocalDateTime endTime;//結束時間
    private Integer[] createUserId;
    private Integer[] deleteUserId;

    public Integer getMeetingroomId() {
        return this.meetingroomId;
    }

    public Integer getMeetingRoomNameId() {
        return this.meetingroomNameId;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public Integer[] getCreateUserId() {
        return this.createUserId;
    }

    public Integer[] getDeleteUserId() {
        return this.deleteUserId;
    }

    public Integer setMeetingRoomId(Integer meetingroomId) {
        return this.meetingroomId = meetingroomId;
    }

    public void setMeetingRoomNameId(Integer meetingroomNameId) {
        this.meetingroomNameId = meetingroomNameId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setCreateUserId(Integer[] createUserId) {
        this.createUserId = createUserId;
    }

    public void setDeleteUserId(Integer[] deleteUserId) {
        this.deleteUserId = deleteUserId;
    }
}
