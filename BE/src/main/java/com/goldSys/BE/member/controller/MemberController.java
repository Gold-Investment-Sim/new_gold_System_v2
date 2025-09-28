package com.goldSys.BE.member.controller;

import com.goldSys.BE.member.dto.LoginRequestDto;
import com.goldSys.BE.member.dto.LoginResponseDto;
import com.goldSys.BE.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private static final String LOGIN_ID = "LOGIN_ID";
    private static final String LOGIN_NO = "LOGIN_NO";

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req, HttpSession session) {
        LoginResponseDto dto = memberService.login(req);
        session.setAttribute(LOGIN_ID, dto.getMemberId());
        session.setAttribute(LOGIN_NO, dto.getMemberNo());
        memberService.updateLastLogin(dto.getMemberId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        var s = session.getAttribute(LOGIN_ID);
        if (s != null) session.invalidate();
        return ResponseEntity.ok().build();
    }
}