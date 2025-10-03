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
        COUNT(*)                                        AS total,
        COALESCE(SUM(CASE WHEN SH.HISTORY_RESULT IS NOT NULL 
                           AND TRIM(SH.HISTORY_PREDICT)=TRIM(SH.HISTORY_RESULT) 
                          THEN 1 ELSE 0 END),0)         AS correct,
        COALESCE(SUM(CASE WHEN SH.HISTORY_RESULT IS NOT NULL 
                           AND TRIM(SH.HISTORY_PREDICT)<>TRIM(SH.HISTORY_RESULT) 
                          THEN 1 ELSE 0 END),0)         AS wrong
      FROM QUOTES_DAILY QD
      LEFT JOIN SIMULATION_HISTORY SH
        ON QD.DATE = SH.HISTORY_DATE
       AND SH.MEMBER_NO = :memberNo
      WHERE QD.DATE BETWEEN :from AND :to
        AND (:type IS NULL OR :type = '' OR SH.HISTORY_TYPE = :type)
      """, nativeQuery = true)
    StatsAgg aggregateStats(@Param("memberNo") Long memberNo,
                            @Param("from") LocalDate from,
                            @Param("to") LocalDate to,
                            @Param("type") String type);
}
