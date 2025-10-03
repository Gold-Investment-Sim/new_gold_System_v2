package com.goldSys.BE.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberAssetDto {
    private Long memberNo;
    private Long balance;

    public static MemberAssetDto of(Long memberNo, Long balance) {
        return new MemberAssetDto(memberNo, balance);
    }
}
