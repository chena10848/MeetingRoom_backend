package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "MEETING_GROUP_USER")
@Data
public class MeetingGroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEETING_GROUP_USER_ID")//會議室群組ID
    private Integer id;

    @Column(name = "MEETING_ROOM_ID", nullable = false)//會議室ID
    private Integer meetingroomId;

    @Column(name = "USERS_ID", nullable = false)//使用者ID
    private Integer userId;

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
