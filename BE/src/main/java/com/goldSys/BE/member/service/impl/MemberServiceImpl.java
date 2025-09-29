package com.goldSys.BE.member.service.impl;

import com.goldSys.BE.member.dto.*;
import com.goldSys.BE.member.entity.Member;
import com.goldSys.BE.member.entity.MemberAsset;
import com.goldSys.BE.member.repository.MemberRepository;
import com.goldSys.BE.member.service.MemberAssetService;
import com.goldSys.BE.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final MemberAssetService assetService; // 주입


    // ====================
    // 회원가입
    // ====================
    @Override
    @Transactional
    public SignupResponseDto join(SignupRequestDto req, String defaultRole) {
        // 중복 체크
        if (repo.findByMemberId(req.getMemberId()).isPresent())
            throw new IllegalArgumentException("DUPLICATE_ID");
        if (repo.findByMemberEmail(req.getMemberEmail()).isPresent())
            throw new IllegalArgumentException("DUPLICATE_EMAIL");

        // 엔티티 생성
        Member m = Member.builder()
                .memberId(req.getMemberId())
                .memberPwd(encoder.encode(req.getMemberPwd()))
                .memberName(req.getMemberName())
                .memberEmail(req.getMemberEmail())
                .memberRole(defaultRole != null ? defaultRole : "ROLE_USER")
                .memberIsActive(true) // 이메일 인증은 프론트에서 끝나고 넘어옴
                .memberCreatedAt(LocalDateTime.now())
                .memberUpdatedAt(LocalDateTime.now())
                .build();

        // 저장
        Member saved = repo.save(m);

        return new SignupResponseDto(
                saved.getMemberNo().intValue(),
                saved.getMemberId(),
                saved.getMemberName(),
                saved.getMemberEmail(),
                saved.getMemberRole()
        );
    }

    // ====================
    // 로그인
    // ====================
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto req) {
        Member member = repo.findByMemberId(req.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자"));

        if (!encoder.matches(req.getMemberPwd(), member.getMemberPwd())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }
        if (!member.getMemberIsActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 인증 필요");
        }

        // 첫 로그인 자산 지급 처리
        assetService.getOrCreateDefault(member.getMemberNo());
        Long balance = assetService.getOrCreateDefault(member.getMemberNo()).getBalance();

        return new LoginResponseDto(
                member.getMemberNo().intValue(),
                member.getMemberId(),
                member.getMemberName(),
                member.getMemberEmail(),
                member.getMemberRole(),
                balance
        );
    }

    // ====================
    // 최근 로그인 갱신
    // ====================
    @Override
    @Transactional
    public void updateLastLogin(String memberId) {
        Member m = repo.findByMemberId(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));
        m.setMemberLastLoginAt(LocalDateTime.now());
    }

    // ====================
    // 아이디/이메일 중복 체크
    // ====================
    @Override
    public boolean checkId(String memberId) {
        if (memberId == null || memberId.isBlank()) return true;
        return repo.findByMemberId(memberId.trim()).isPresent();
    }

    @Override
    public boolean checkEmail(String memberEmail) {
        if (memberEmail == null || memberEmail.isBlank()) return true;
        return repo.findByMemberEmail(memberEmail.trim()).isPresent();
    }

    // ====================
    // 비밀번호 초기화(임시 비번 발급)
    // ====================
    @Override
    @Transactional
    public void forgotPassword(String memberId, String memberEmail) {
        if (memberId == null || memberId.isBlank() || memberEmail == null || memberEmail.isBlank()) {
            throw new IllegalArgumentException("아이디 또는 이메일 불일치");
        }

        Member m = repo.findByMemberId(memberId.trim())
                .filter(user -> user.getMemberEmail().equalsIgnoreCase(memberEmail.trim()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 이메일 불일치"));

        String tempPassword = generatePassword(12);
        String encodedPassword = encoder.encode(tempPassword);
        m.setMemberPwd(encodedPassword);

        // 메일 발송
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(m.getMemberEmail());
        msg.setSubject("임시 비밀번호 안내");
        msg.setText("임시 비밀번호: " + tempPassword + "\n로그인 후 반드시 비밀번호를 변경하세요.");
        mailSender.send(msg);
    }

    // ====================
    // 비밀번호 변경
    // ====================
    @Override
    @Transactional
    public void updatePassword(String memberId, String currentPwd, String newPwd) {
        Member m = repo.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        if (!encoder.matches(currentPwd, m.getMemberPwd())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (encoder.matches(newPwd, m.getMemberPwd())) {
            throw new IllegalArgumentException("이전과 동일한 비밀번호는 사용할 수 없습니다.");
        }

        m.setMemberPwd(encoder.encode(newPwd));
    }

    // ====================
    // 회원 탈퇴
    // ====================
    @Override
    @Transactional
    public void deleteAccount(String memberId, String currentPwd) {
        Member m = repo.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        if (!encoder.matches(currentPwd, m.getMemberPwd())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        m.setMemberIsActive(false);
        m.setMemberDeletedAt(LocalDateTime.now());
    }

    // ====================
    // 임시 비밀번호 생성 유틸
    // ====================
    private String generatePassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&*?";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto getMe(Long memberNo) {
        // repo = MemberRepository (이미 필드로 주입돼 있음)
        Member m = repo.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));


        Long balance = assetService.getOrCreateDefault(memberNo).getBalance();

        return new LoginResponseDto(
                m.getMemberNo().intValue(),
                m.getMemberId(),
                m.getMemberName(),
                m.getMemberEmail(),
                m.getMemberRole(),
                balance
        );
    }
}

