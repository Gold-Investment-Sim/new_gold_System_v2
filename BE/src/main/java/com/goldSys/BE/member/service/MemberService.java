package com.goldSys.BE.member.service;

import com.goldSys.BE.member.dto.LoginRequestDto;
import com.goldSys.BE.member.dto.LoginResponseDto;

public interface MemberService {
    LoginResponseDto login(LoginRequestDto req);
    void updateLastLogin(String memberId);
}