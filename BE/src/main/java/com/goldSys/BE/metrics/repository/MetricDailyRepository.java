package com.goldSys.BE.metrics.repository;

import com.goldSys.BE.history.entity.QuotesDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MetricDailyRepository extends JpaRepository<QuotesDaily, Long> {
    List<QuotesDaily> findByDateBetweenOrderByDateAsc(LocalDate from, LocalDate to);
}