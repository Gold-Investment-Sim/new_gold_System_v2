package com.goldSys.BE.member.controller;

import com.goldSys.BE.member.dto.*;
import com.goldSys.BE.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

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
        session.invalidate();
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

    // 비밀번호 변경
    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequestDto body, HttpSession session) {
        Object uid = session.getAttribute(LOGIN_ID);
        if (uid == null) return ResponseEntity.status(401).body(Map.of("ok", false, "message", "로그인이 필요합니다."));

        String currentPwd = body.getCurrentPwd();
        String newPwd     = body.getNewPwd();
        String confirmPwd = body.getConfirmPwd();

        if (newPwd == null || !newPwd.equals(confirmPwd)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "새 비밀번호가 일치하지 않습니다."));
        }

        try {
            memberService.updatePassword(uid.toString(), currentPwd, newPwd);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("ok", false, "message", "서버 오류"));
        }
    }

    // 비밀번호 일치확인
    @PostMapping("/checkPassword")
    public ResponseEntity<?> checkPassword(@RequestBody PasswordOnlyRequestDto body, HttpSession session) {
        Object uid = session.getAttribute(LOGIN_ID);
        if (uid == null)
            return ResponseEntity.status(401).body(Map.of("ok", false, "message", "로그인이 필요합니다."));
        boolean ok = memberService.checkPassword(uid.toString(), body.getPassword());
        return ok
                ? ResponseEntity.ok(Map.of("ok", true))
                : ResponseEntity.badRequest().body(Map.of("ok", false, "message", "비밀번호가 일치하지 않습니다."));
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequestDto body, HttpSession session) {
        Object uid = session.getAttribute(LOGIN_ID);
        if (uid == null)
            return ResponseEntity.status(401).body(Map.of("ok", false, "message", "로그인이 필요합니다."));
        try {
            memberService.deleteAccount(uid.toString(), body.getPassword());
            session.invalidate();
            return ResponseEntity.ok(Map.of("ok", true, "message", "계정 비활성화 완료"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("ok", false, "message", "서버 오류"));
        }
    }
}