package com.goldSys.BE.history.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HistoryListDto {
    private List<SimulationHistoryDto> items;
    private int page;   // 1-base
    private int size;
    private long total;
}