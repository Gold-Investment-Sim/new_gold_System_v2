package com.goldSys.BE.member.controller;

import com.goldSys.BE.member.dto.*;
import com.goldSys.BE.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private static final String LOGIN_ID = "LOGIN_ID";
    private static final String LOGIN_NO = "LOGIN_NO";

    private final MemberService memberService;


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req, HttpSession session) {
        LoginResponseDto dto = memberService.login(req);
        session.setAttribute(LOGIN_ID, dto.getMemberId());
        session.setAttribute(LOGIN_NO, dto.getMemberNo());
        memberService.updateLastLogin(dto.getMemberId());
        return ResponseEntity.ok(dto);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        var s = session.getAttribute(LOGIN_ID);
        if (s != null) session.invalidate();
        return ResponseEntity.ok().build();
    }

    // 아이디 중복체크
    @GetMapping("/check-id")
    public ResponseEntity<ExistsResponseDto> checkId(@RequestParam String memberId) {
        return ResponseEntity.ok(ExistsResponseDto.of(memberService.checkId(memberId)));
    }

    // 이메일 중복체크
    @GetMapping("/check-email")
    public ResponseEntity<ExistsResponseDto> checkEmail(@RequestParam String memberEmail) {
        return ResponseEntity.ok(ExistsResponseDto.of(memberService.checkEmail(memberEmail)));
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<SignupResponseDto> join(@RequestBody SignupRequestDto req) {
        SignupResponseDto dto = memberService.join(req, "ROLE_USER");
        return ResponseEntity.ok(dto);
    }

    // 로그인 계속 지속
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object uno = session.getAttribute(LOGIN_NO);
        if (uno == null) return ResponseEntity.status(401).build();
        Long memberNo = Long.valueOf(uno.toString());
        LoginResponseDto dto = memberService.getMe(memberNo);
        return ResponseEntity.ok(dto);
    }


}