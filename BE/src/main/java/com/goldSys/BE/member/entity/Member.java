package com.goldSys.BE.member.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_NO")
    private Long memberNo;

    @Column(name = "MEMBER_ID", nullable = false, unique = true, length = 50)
    private String memberId;

    @Column(name = "MEMBER_PWD", nullable = false, length = 255)
    private String memberPwd;

    @Column(name = "MEMBER_NAME", nullable = false, length = 100)
    private String memberName;

    @Column(name = "MEMBER_EMAIL", nullable = false, unique = true, length = 100)
    private String memberEmail;

    @Column(name = "MEMBER_ROLE", length = 20, nullable = false)
    private String memberRole = "USER";

    @Column(name = "MEMBER_CREATED_AT", updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime memberCreatedAt;

    @Column(name = "MEMBER_UPDATED_AT",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime memberUpdatedAt;

    @Column(name = "MEMBER_LAST_LOGIN_AT")
    private LocalDateTime memberLastLoginAt;

    @Column(name = "MEMBER_IS_ACTIVE", nullable = false)
    private Boolean memberIsActive = true;

    @Column(name = "MEMBER_DELETED_AT")
    private LocalDateTime memberDeletedAt;

    // == Lifecycle Callbacks ==
    @PrePersist
    protected void onCreate() {
        if (this.memberCreatedAt == null) {
            this.memberCreatedAt = LocalDateTime.now();
        }
        if (this.memberUpdatedAt == null) {
            this.memberUpdatedAt = LocalDateTime.now();
        }
        if (this.memberIsActive == null) {
            this.memberIsActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.memberUpdatedAt = LocalDateTime.now();
    }
}