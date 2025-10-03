package com.goldSys.BE.member.service;

public interface MemberAssetService {
    Long getOrCreateDefault(Long memberNo);  // balance(Long)만 반환
    Long getBalance(Long memberNo);          // balance(Long)만 반환
}
