package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEETING_ROOM")
@Data
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEETING_ROOM_ID")//會議室ID
    private Integer id;

    @Column(name = "MEETING_ROOM_NAME_ID") //會議室名稱ID
    private Integer meetingRoomNameId;

    @Column(name = "LESSEE_START_TIME")//租用開始時間
    private LocalDateTime lesseeStartTime;

    @Column(name = "LESSEE_END_TIME")//結束時間
    private LocalDateTime lesseeEndTime;

    @Column(name = "LESSEE_USERS_ID")//租用人
    private Integer lesseeUserId;

    @Column(name = "CREATEDAT", nullable = false, updatable = false)//新增時間
    private LocalDateTime createdAt;

    @Column(name = "UPDATEDAT")//更新時間
    private LocalDateTime updatedAt;

    // 在新增資料前自動設定 CreatedAt 和 UpdatedAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 在更新資料前自動更新 UpdatedAt
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
