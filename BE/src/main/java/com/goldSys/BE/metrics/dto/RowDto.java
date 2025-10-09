package com.goldSys.BE.metrics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RowDto {
    private String date;

    @JsonProperty("KRW_G_OPEN")  private double krw_g_open;
    @JsonProperty("FX_RATE")     private double fx_rate;
    @JsonProperty("VIX")         private double vix;
    @JsonProperty("ETF_VOLUME")  private double etf_volume;
    @JsonProperty("KRW_G_CLOSE") private double krw_g_close;
}
