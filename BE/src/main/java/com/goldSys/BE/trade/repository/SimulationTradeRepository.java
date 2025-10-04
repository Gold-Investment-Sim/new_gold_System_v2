package com.goldSys.BE.trade.repository;

import com.goldSys.BE.trade.entity.SimulationTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationTradeRepository extends JpaRepository<SimulationTrade, Long> {
}
