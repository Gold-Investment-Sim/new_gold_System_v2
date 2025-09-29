package com.goldSys.BE.member.entity;

import lombok.Data;

@Data
public class MemberAuth {
    private Long no;         // PK
    private String memberId; // FK (회원 아이디)
    private String auth;     // 권한
}
