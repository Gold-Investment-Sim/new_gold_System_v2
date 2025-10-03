package com.goldSys.BE.history.repository;

import com.goldSys.BE.history.entity.SimulationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationHistoryRepository
        extends JpaRepository<SimulationHistory, Long>, JpaSpecificationExecutor<SimulationHistory> {

    interface SummaryAgg {
        long getTotal();
        Long getCorrect();
        Long getWrong();
        Double getTotalPnl();
        Double getAvgPnl();
        Double getMaxPnl();
        Double getMinPnl();
    }

    @Query("""
      select
        count(h) as total,
        sum(case when h.historyResult = h.historyPredict then 1 else 0 end) as correct,
        sum(case when h.historyResult <> h.historyPredict then 1 else 0 end) as wrong,
        sum(coalesce(h.pnl,0)) as totalPnl,
        avg(coalesce(h.pnl,0)) as avgPnl,
        max(coalesce(h.pnl,0)) as maxPnl,
        min(coalesce(h.pnl,0)) as minPnl
      from SimulationHistory h
      where h.memberNo = :memberNo
    """)
    SummaryAgg aggregateSummaryByMember(@Param("memberNo") Long memberNo);
}