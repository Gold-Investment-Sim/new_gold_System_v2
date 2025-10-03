package com.goldSys.BE.metrics.repository;

import com.goldSys.BE.history.entity.QuotesDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface QuotesDailyRepository extends JpaRepository<QuotesDaily, LocalDate> {
    List<QuotesDaily> findByDateBetweenOrderByDate(LocalDate from, LocalDate to);
}