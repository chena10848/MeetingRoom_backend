package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEETING_ROOM_NAME")
@Data
public class MeetingRoomName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEETING_ROOM_NAME_ID")//會議室名稱ID
    private Integer id;

    @Column(name = "MEETING_ROOM_NAME", nullable = false)//會議室名稱
    private String meetingroomName;

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
