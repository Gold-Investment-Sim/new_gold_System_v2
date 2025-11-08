package com.goldSys.BE.metrics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * 개발자 : 최승희
 * LSTM 예측 응답 DTO
 * 구성 : 예측 결과 시점(timestamps) + 예측값(yPred)
 * 사용 : FastAPI 모델 서버로부터 예측 결과 수신 시 응답 매핑 (/api/lstm)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictRes {
    private List<String> timestamps;

    @JsonProperty("y_pred")   // JSON 키는 y_pred지만
    private List<Double> yPred; // 자바에서는 yPred로 받기
}