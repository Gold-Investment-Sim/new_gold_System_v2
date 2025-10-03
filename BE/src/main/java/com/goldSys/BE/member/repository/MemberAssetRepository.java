package com.goldSys.BE.member.repository;

import com.goldSys.BE.member.entity.MemberAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberAssetRepository extends JpaRepository<MemberAsset, Long> {

    // balance(Long)만 바로 조회
    @Query("select a.balance from MemberAsset a where a.member.memberNo = :memberNo")
    Optional<Long> findBalanceByMemberNo(@Param("memberNo") Long memberNo);

    // 필요 시 전체 엔티티 조회 (안쓰면 생략 가능)
    Optional<MemberAsset> findByMember_MemberNo(Long memberNo);
}
