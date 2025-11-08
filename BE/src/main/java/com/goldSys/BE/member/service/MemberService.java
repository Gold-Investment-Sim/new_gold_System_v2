package com.goldSys.BE.member.service;

import com.goldSys.BE.member.dto.LoginRequestDto;
import com.goldSys.BE.member.dto.LoginResponseDto;
import com.goldSys.BE.member.dto.SignupRequestDto;
import com.goldSys.BE.member.dto.SignupResponseDto;

/**
 * 개발자 : 최승희
 * 회원 서비스 인터페이스
 *
 * 주요 기능
 * 1. 아이디 / 이메일 / 비밀번호 검증
 * 2. 회원가입 및 로그인 처리
 * 3. 마지막 로그인 시간 업데이트
 * 4. 비밀번호 변경, 계정 비활성화, 비밀번호 찾기
 * 5. 세션 사용자 정보 조회(getMe)
 *
 * 사용
 * MemberController
 */
public interface MemberService {
    boolean checkId(String memberId); // 아이디 중복 여부 확인
    boolean checkEmail(String memberEmail); // 이메일 중복 여부 확인
    boolean checkPassword(String memberId, String password); // 비밀번호 일치 여부 확인

    SignupResponseDto join(SignupRequestDto req, String defaultRole); // 회원가입 처리

    LoginResponseDto login(LoginRequestDto req);  // 로그인 처리

    void updateLastLogin(String memberId); // 마지막 로그인 시간 업데이트

    void updatePassword(String memberId, String currentPwd, String newPwd); // 비밀번호 변경

    void deleteAccount(String memberId, String password); // 계정 비활성화(탈퇴)

    void forgotPassword(String memberId, String memberEmail); // 비밀번호 찾기(임시 비밀번호 발급 등)

    LoginResponseDto getMe(Long memberNo); // 회원 번호로 현재 사용자 정보 조회


}