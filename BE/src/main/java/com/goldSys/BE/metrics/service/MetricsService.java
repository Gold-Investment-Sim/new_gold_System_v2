package com.goldSys.BE.metrics.service;

import com.goldSys.BE.metrics.dto.SeriesPointDto;

import java.time.LocalDate;
import java.util.List;

/**
 * 개발자 : 최승희
 * 메트릭(지표) 서비스 인터페이스
 *
 * 주요 기능
 * 1. getSeries : 지정된 기간(from~to) 동안 특정 지표(metric)의 시계열 데이터 조회
 *
 * 사용
 * MetricsController
 */
public interface MetricsService {
    /**
     * 메트릭 시계열 데이터 조회
     * @param metric 조회할 지표명 (예: gold, usd, oil 등)
     * @param from   조회 시작일 (예: 2023-01-01)
     * @param to     조회 종료일 (예: 2024-12-31)
     * @return       [{ "date": "2024-01-01", "value": 1234.56 }, ...] 형태의 시계열 데이터 리스트
     */
    List<SeriesPointDto> getSeries(String metric, LocalDate from, LocalDate to);
}