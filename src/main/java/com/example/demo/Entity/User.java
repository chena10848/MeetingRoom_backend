package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity
@Table(name = "USERS")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")//使用者 ID
    private Integer id;

    @Column(name = "USER_NAME", nullable = false)//使用者名稱
    private String userName;

    @Column(name = "EMAIL", nullable = false, unique = true) //Email
    private String email;

    @Column(name = "PASSWORD") //密碼
    private String password;

    @Column(name = "LOGIN_TYPE", nullable = false)
    private String loginType;

    @Column(name = "CREATEDAT", updatable = false)//新增時間
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
