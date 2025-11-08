package com.goldSys.BE.history.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

/**
 * 개발자 : 최승희
 * 투자 이력 통계 조회 Repository
 * 관련 엔티티
 * 1. QuotesDaily : 10년치 일별 시세 데이터 테이블
 * 2. TradeHistoryRow : 회원별 거래 내역(매수/매도)을 저장
 * 사용 : 투자 통계 조회 API (/api/history/stats)
 */
@Repository
public interface HistoryStatsRepository extends JpaRepository<com.goldSys.BE.history.entity.QuotesDaily, LocalDate> {

    interface StatsAgg {
        long getTotal();
        long getCorrect();
        long getWrong();
    }

    /**
     * 개발자 : 최승희
     * 회원/기간/거래유형별 거래 통계를 집계하는 쿼리
     */
    @Query(value = """
        SELECT
          COUNT(*) AS total,
          COALESCE(SUM(CASE WHEN T.day_result =  1 THEN 1 ELSE 0 END), 0) AS correct,
          COALESCE(SUM(CASE WHEN T.day_result = -1 THEN 1 ELSE 0 END), 0) AS wrong
        FROM QUOTES_DAILY QD
        LEFT JOIN (
          SELECT
            st.TRADE_DATE,
            st.MEMBER_NO,
            CASE
              WHEN SUM(CASE WHEN st.PNL > 0 THEN 1 ELSE 0 END) > 0 THEN  1
              WHEN SUM(CASE WHEN st.PNL < 0 THEN 1 ELSE 0 END) > 0 THEN -1
              ELSE 0
            END AS day_result
          FROM SIMULATION_TRADE st
          WHERE st.MEMBER_NO = :memberNo
            AND st.TRADE_DATE BETWEEN :from AND :to
            AND (:type IS NULL OR :type = '' OR st.TRADE_TYPE = :type)
          GROUP BY st.MEMBER_NO, st.TRADE_DATE
        ) T
          ON T.TRADE_DATE = QD.DATE AND T.MEMBER_NO = :memberNo
        WHERE QD.DATE BETWEEN :from AND :to
        """, nativeQuery = true)
    StatsAgg aggregateStats(@Param("memberNo") Long memberNo,
                            @Param("from") LocalDate from,
                            @Param("to") LocalDate to,
                            @Param("type") String type);
}
