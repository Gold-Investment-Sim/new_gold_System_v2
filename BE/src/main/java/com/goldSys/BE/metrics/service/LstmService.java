package com.goldSys.BE.metrics.service;

import java.util.List;
import java.util.Map;

/**
 * 개발자 : 최승희
 * LSTM 예측 서비스 인터페이스
 *
 * 주요 기능
 * 1. getPredSeries : 지정된 기간(from~to)의 예측 시계열 데이터 조회
 * 2. getPredAll    : 기준일(to) 기준 전체 메트릭(gold, usd, oil 등) 예측 데이터 조회
 *
 * 사용
 * LstmController
 */
public interface LstmService {
    /**
     * LSTM 예측 시계열 조회
     * @param from 조회 시작일 (예: 2023-01-01)
     * @param to   조회 종료일 (예: 2024-12-31)
     * @return     [{ "date": "2024-01-01", "value": 1234.56 }, ...] 형태의 예측 결과 리스트
     */
    List<Map<String, Object>> getPredSeries(String from, String to);

    /**
     * 전체 메트릭 예측 데이터 조회
     * @param to 조회 기준일 (예: 2024-12-31)
     * @return   [{ "metric": "gold", "date": "...", "value": ... }, ...] 형태의 전체 예측 데이터 리스트
     */
    List<Map<String, Object>> getPredAll(String to);
}