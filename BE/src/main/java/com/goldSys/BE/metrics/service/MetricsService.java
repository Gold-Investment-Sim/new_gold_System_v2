package com.goldSys.BE.metrics.service;

import com.goldSys.BE.metrics.dto.SeriesPointDto;

import java.time.LocalDate;
import java.util.List;

public interface MetricsService {
    List<SeriesPointDto> getSeries(String metric, LocalDate from, LocalDate to);
}