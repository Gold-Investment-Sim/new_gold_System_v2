package com.goldSys.BE.controller;

import com.goldSys.BE.entity.Member;
import com.goldSys.BE.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // React 허용
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    // 로그인 API
    @PostMapping("/login")
    public String login(@RequestBody Member request) {
        Member member = memberRepository.findByMemberId(request.getMemberId());
        if (member != null && member.getMemberPwd().equals(request.getMemberPwd())) {
            return "로그인 성공";
        } else {
            return "로그인 실패";
        }
    }
}
