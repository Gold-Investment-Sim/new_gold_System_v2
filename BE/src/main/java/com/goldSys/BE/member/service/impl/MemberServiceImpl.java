package com.goldSys.BE.member.service.impl;

import com.goldSys.BE.member.dto.LoginRequestDto;
import com.goldSys.BE.member.dto.LoginResponseDto;
import com.goldSys.BE.member.entity.Member;
import com.goldSys.BE.member.repository.MemberRepository;
import com.goldSys.BE.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository repo;
    private final PasswordEncoder encoder;

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto req) {
        Member m = repo.findByMemberId(req.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자"));
        if (!encoder.matches(req.getMemberPwd(), m.getMemberPwd())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }
        return new LoginResponseDto(
                m.getMemberNo().intValue(),
                m.getMemberId(),
                m.getMemberName(),
                m.getMemberEmail(),
                m.getMemberRole()
        );
    }

    @Override
    @Transactional
    public void updateLastLogin(String memberId) {
        Member m = repo.findByMemberId(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));
        m.setMemberLastLoginAt(LocalDateTime.now());  // 필드명과 setter 일치
    }

}