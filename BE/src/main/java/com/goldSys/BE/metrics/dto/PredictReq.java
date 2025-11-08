package com.goldSys.BE.metrics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * 개발자 : 최승희
 * LSTM 예측 요청 DTO
 * 구성 : 예측 입력 데이터 리스트 + 예측 옵션(returnLastN, nextDay)
 * 사용 : FastAPI 모델 서버에 예측 요청 시 전송 (/api/lstm)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictReq {
    private List<RowDto> rows;

    @JsonProperty("return_last_n")
    private Integer returnLastN;

    @JsonProperty("next_day")
    private Boolean nextDay;
}