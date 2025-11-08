package com.goldSys.BE.history.repository;

import com.goldSys.BE.history.entity.TradeHistoryRow;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * 개발자 : 최승희
 * 투자 이력 조회용 Repository
 * 관련 엔티티
 * TradeHistoryRow (simulation_trade 테이블)
 */
@Repository
public interface TradeHistoryRowRepository
        extends JpaRepository<TradeHistoryRow, Long>, JpaSpecificationExecutor<TradeHistoryRow> {}
