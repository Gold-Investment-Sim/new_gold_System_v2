package com.goldSys.BE.metrics.service;

import java.util.List;
import java.util.Map;

public interface LstmService {
    List<Map<String, Object>> getPredSeries(String from, String to); // 구간 예측(옵션)
    List<Map<String, Object>> getPredAll(String to);                 // 전구간 예측(t-1까지)
}