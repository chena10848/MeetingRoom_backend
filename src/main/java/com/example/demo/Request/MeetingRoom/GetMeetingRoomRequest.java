package com.example.demo.Request.MeetingRoom;

import java.security.Timestamp;

public class GetMeetingRoomRequest {

    private Timestamp startTime;
    private Timestamp endTime;

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartTime(){
        return this.startTime;
    }

    public Timestamp getEndTime() {
        return this.endTime;
    }
}
