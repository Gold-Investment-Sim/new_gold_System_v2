package com.goldSys.BE.member.service.impl;

import com.goldSys.BE.member.entity.Member;
import com.goldSys.BE.member.entity.MemberAsset;
import com.goldSys.BE.member.repository.MemberAssetRepository;
import com.goldSys.BE.member.repository.MemberRepository;
import com.goldSys.BE.member.service.MemberAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAssetServiceImpl implements MemberAssetService {
    private final MemberAssetRepository assetRepo;
    private final MemberRepository memberRepo;
    private static final long DEFAULT_BALANCE = 3_000_000L;

    @Override
    @Transactional
    public Long getOrCreateDefault(Long memberNo) {
        return assetRepo.findBalanceByMemberNo(memberNo)
                .orElseGet(() -> {
                    Member m = memberRepo.findById(memberNo)
                            .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
                    MemberAsset a = MemberAsset.builder()
                            .member(m)
                            .balance(DEFAULT_BALANCE)
                            .build();
                    assetRepo.save(a);
                    return DEFAULT_BALANCE;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Long getBalance(Long memberNo) {
        return assetRepo.findBalanceByMemberNo(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("자산 없음"));
    }
}
