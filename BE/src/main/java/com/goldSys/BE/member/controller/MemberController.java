package com.goldSys.BE.member.controller;

import com.goldSys.BE.member.dto.*;
import com.goldSys.BE.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 개발자 : 최승희
 * 회원 인증 및 계정 관리 컨트롤러
 * 엔드포인트: /api/auth
 *
 * 역할
 * 1. 로그인 / 로그아웃 / 회원가입 처리
 * 2. 아이디·이메일 중복 확인
 * 3. 비밀번호 변경 및 검증
 * 4. 계정 탈퇴 및 비밀번호 찾기
 * 5. 세션 기반 사용자 인증 관리 (LOGIN_ID, LOGIN_NO)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private static final String LOGIN_ID = "LOGIN_ID"; // 세션에 저장할 사용자 ID
    private static final String LOGIN_NO = "LOGIN_NO"; // 세션에 저장할 사용자 번호

    private final MemberService memberService;


    /**
     * 로그인
     * @param req 로그인 요청 DTO (아이디, 비밀번호)
     * @param session HttpSession 객체
     * @return 로그인 결과 DTO + 세션 저장
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto req, HttpSession session) {
        LoginResponseDto dto = memberService.login(req);
        session.setAttribute(LOGIN_ID, dto.getMemberId());
        session.setAttribute(LOGIN_NO, dto.getMemberNo());
        memberService.updateLastLogin(dto.getMemberId());
        return ResponseEntity.ok(dto);
    }

    /**
     * 로그아웃
     * @param session 현재 로그인된 세션
     * @return 세션 만료 후 200 OK
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    /**
     * 아이디 중복 확인
     * @param memberId 사용자 ID
     * @return 중복 여부(Boolean) 응답
     */
    @GetMapping("/check-id")
    public ResponseEntity<ExistsResponseDto> checkId(@RequestParam String memberId) {
        return ResponseEntity.ok(ExistsResponseDto.of(memberService.checkId(memberId)));
    }

    /**
     * 이메일 중복 확인
     * @param memberEmail 사용자 이메일
     * @return 중복 여부(Boolean) 응답
     */
    @GetMapping("/check-email")
    public ResponseEntity<ExistsResponseDto> checkEmail(@RequestParam String memberEmail) {
        return ResponseEntity.ok(ExistsResponseDto.of(memberService.checkEmail(memberEmail)));
    }

    /**
     * 회원가입
     * @param req 회원가입 요청 DTO
     * @return 회원가입 결과 DTO
     */
    @PostMapping("/join")
    public ResponseEntity<SignupResponseDto> join(@RequestBody SignupRequestDto req) {
        SignupResponseDto dto = memberService.join(req, "ROLE_USER");
        return ResponseEntity.ok(dto);
    }

    /**
     * 로그인 유지 확인 (세션 정보 기반)
     * @param session 현재 세션
     * @return 로그인된 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object uno = session.getAttribute(LOGIN_NO);
        if (uno == null) return ResponseEntity.status(401).build();
        Long memberNo = Long.valueOf(uno.toString());
        LoginResponseDto dto = memberService.getMe(memberNo);
        return ResponseEntity.ok(dto);
    }

    /**
     * 비밀번호 변경
     * @param body 요청 DTO (현재 비밀번호, 새 비밀번호, 확인 비밀번호)
     * @param session 현재 세션
     * @return 처리 결과 Map(ok, message)
     */
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

    /**
     * 비밀번호 일치 확인
     * @param body 비밀번호 요청 DTO
     * @param session 현재 세션
     * @return 비밀번호 일치 여부 Map(ok, message)
     */
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

    /**
     * 회원 탈퇴 (비활성화)
     * @param body 요청 DTO (비밀번호 포함)
     * @param session 현재 세션
     * @return 처리 결과 Map(ok, message)
     */
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

    /**
     * 비밀번호 찾기 (임시 비밀번호 발급)
     * @param body 요청 DTO (아이디, 이메일)
     * @return 처리 결과 Map(ok, message)
     */
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDto body) {
        try {
            memberService.forgotPassword(body.getMemberId(), body.getMemberEmail());
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("ok", false, "message", "서버 오류"));
        }
    }
}