package com.goldSys.BE.member.repository;

import com.goldSys.BE.member.entity.MemberAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberAssetRepository extends JpaRepository<MemberAsset, Long> {
    boolean existsByMember_MemberNo(Long memberNo);
    Optional<MemberAsset> findByMember_MemberNo(Long memberNo);

    @Query("select a from MemberAsset a where a.member.memberNo = :memberNo")
    Optional<MemberAsset> findByMemberNo(@Param("memberNo") Long memberNo); // ← 쓰고 싶으면 이렇게

}