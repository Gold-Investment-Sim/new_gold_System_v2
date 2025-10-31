package com.goldSys.BE.history.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface HistoryStatsRepository extends JpaRepository<com.goldSys.BE.history.entity.QuotesDaily, LocalDate> {

    interface StatsAgg {
        long getTotal();
        long getCorrect();
        long getWrong();
    }

    @Query(value = """
        SELECT
          COUNT(*) AS total,  -- 기간 내 장 개장일 수
          COALESCE(SUM(CASE WHEN T.day_result =  1 THEN 1 ELSE 0 END), 0) AS correct,
          COALESCE(SUM(CASE WHEN T.day_result = -1 THEN 1 ELSE 0 END), 0) AS wrong
        FROM QUOTES_DAILY QD
        LEFT JOIN (
          /* 하루 결과 압축: + 있으면 정답, - 있으면 오답, 아니면 0(미풀이) */
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
