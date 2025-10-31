package com.goldSys.BE.history.repository;

import com.goldSys.BE.history.entity.TradeHistoryRow;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeHistoryRowRepository
        extends JpaRepository<TradeHistoryRow, Long>, JpaSpecificationExecutor<TradeHistoryRow> {}
