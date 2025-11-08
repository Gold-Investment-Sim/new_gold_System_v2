package com.goldSys.BE.metrics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 개발자 : 최승희
 * LSTM 입력 데이터 행 DTO
 * 구성 : 날짜(date) + 주요 지표(금 시세, 환율, 변동성, 거래량 등)
 * 사용 : FastAPI 모델 서버에 예측 요청 시 입력 데이터(rows) 구성 요소로 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RowDto {
    private String date;

    @JsonProperty("KRW_G_OPEN")
    private double krw_g_open;

    @JsonProperty("FX_RATE")
    private double fx_rate;

    @JsonProperty("VIX")
    private double vix;

    @JsonProperty("ETF_VOLUME")
    private double etf_volume;

    @JsonProperty("KRW_G_CLOSE")
    private double krw_g_close;
}
