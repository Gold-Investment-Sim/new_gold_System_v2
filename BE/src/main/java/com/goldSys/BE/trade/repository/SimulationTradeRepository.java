package com.goldSys.BE.trade.repository;

import com.goldSys.BE.trade.entity.SimulationTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface SimulationTradeRepository extends JpaRepository<SimulationTrade, Long> {

    /**
     * 회원의 총 보유 금량(g) 계산
     * - 매수(매입)는 +, 매도(매출)는 -
     */
    @Query("""
        SELECT COALESCE(SUM(
            CASE 
              WHEN t.tradeType = '매수' THEN t.quantity
              WHEN t.tradeType = '매도' THEN -t.quantity
              ELSE 0
            END
        ), 0)
        FROM SimulationTrade t
        WHERE t.member.memberNo = :memberNo
    """)
    BigDecimal findTotalGoldByMemberNo(@Param("memberNo") Long memberNo);
}
