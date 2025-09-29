package com.goldSys.BE.member.service;

import com.goldSys.BE.member.dto.MemberAssetDto;

public interface MemberAssetService {
    MemberAssetDto getOrCreateDefault(Long memberNo);
    MemberAssetDto getBalance(Long memberNo);
}