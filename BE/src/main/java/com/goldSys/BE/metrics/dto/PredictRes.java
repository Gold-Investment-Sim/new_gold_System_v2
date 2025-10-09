package com.goldSys.BE.metrics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


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