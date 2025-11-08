package com.goldSys.BE.history.repository;

import com.goldSys.BE.trade.entity.SimulationTrade;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * 개발자 : 최승희
 * 투자 성과 요약 조회 Repository
 * 관련 엔티티
 * TradeHistoryRow (simulation_trade 테이블)
 * 사용 : 투자 성과 요약 조회 API (/api/history/summary)
 */
@Repository
public interface HistorySummaryRepository extends JpaRepository<SimulationTrade, Long> {

    interface Agg {
        long   getTotal();
        Long   getBuy();
        Long   getSell();
        Double getAvgAmount();

        Double getTotalPnl();
        Double getAvgPnl();
        Double getMaxPnl();
        Double getMinPnl();
    }

    @Query(value = """
      SELECT
        COUNT(*)                                             AS total,
        SUM(CASE WHEN t.TRADE_TYPE = '매수' THEN 1 ELSE 0 END) AS buy,
        SUM(CASE WHEN t.TRADE_TYPE = '매도' THEN 1 ELSE 0 END) AS sell,
        COALESCE(AVG(t.AMOUNT), 0)                           AS avgAmount,
        COALESCE(SUM(t.PNL), 0)                              AS totalPnl,
        COALESCE(AVG(t.PNL), 0)                              AS avgPnl,
        COALESCE(MAX(t.PNL), 0)                              AS maxPnl,
        COALESCE(MIN(t.PNL), 0)                              AS minPnl
      FROM SIMULATION_TRADE t
      WHERE t.MEMBER_NO = :memberNo
        AND t.TRADE_DATE BETWEEN :from AND :to
      """, nativeQuery = true)
    Agg aggregate(@Param("memberNo") Long memberNo,
                  @Param("from") LocalDate from,
                  @Param("to") LocalDate to);
}
