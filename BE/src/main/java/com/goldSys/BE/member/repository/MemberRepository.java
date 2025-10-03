package com.goldSys.BE.member.repository;

import com.goldSys.BE.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByMemberEmail(String memberEmail);
    Optional<Member> findByMemberIdAndMemberEmailIgnoreCase(String memberId, String memberEmail);

}