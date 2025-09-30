package com.goldSys.BE.member.service;

import com.goldSys.BE.member.dto.LoginRequestDto;
import com.goldSys.BE.member.dto.LoginResponseDto;
import com.goldSys.BE.member.dto.SignupRequestDto;
import com.goldSys.BE.member.dto.SignupResponseDto;

public interface MemberService {
    boolean checkId(String memberId);
    boolean checkEmail(String memberEmail);
    boolean checkPassword(String memberId, String password);

    SignupResponseDto join(SignupRequestDto req, String defaultRole);

    LoginResponseDto login(LoginRequestDto req);

    void updateLastLogin(String memberId);

    void updatePassword(String memberId, String currentPwd, String newPwd);

    void deleteAccount(String memberId, String password);

    void forgotPassword(String memberId, String email);

    LoginResponseDto getMe(Long memberNo);


}