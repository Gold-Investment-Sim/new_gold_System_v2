package com.goldSys.BE.metrics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PredictReq {
    private List<RowDto> rows;

    @JsonProperty("return_last_n")
    private Integer returnLastN;

    @JsonProperty("next_day")
    private Boolean nextDay;
}