package com.goldSys.BE.member.repository;

import com.goldSys.BE.member.entity.MemberAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface MemberAssetRepository extends JpaRepository<MemberAsset, Long> {

    // balance(Long)만 바로 조회
    @Query("select a.balance from MemberAsset a where a.member.memberNo = :memberNo")
    Optional<Long> findBalanceByMemberNo(@Param("memberNo") Long memberNo);

    // 전체 엔티티 조회
    Optional<MemberAsset> findByMember_MemberNo(Long memberNo);

    // ✅ balance 업데이트 쿼리 추가
    @Modifying(clearAutomatically = true)
    @Query("UPDATE MemberAsset a SET a.balance = :balance, a.updatedAt = CURRENT_TIMESTAMP WHERE a.member.memberNo = :memberNo")
    int updateBalance(@Param("memberNo") Long memberNo, @Param("balance") BigDecimal balance);
}
