// com.goldSys.BE.history.dto.HistorySummaryDto
package com.goldSys.BE.history.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HistorySummaryDto {
    private long total;        // 총 거래 횟수
    private long buy;          // 매수 건수
    private long sell;         // 매도 건수
    private double avgAmount;  // 평균 거래 금액(원)
    private double totalPnl;   // 누적 수익률 합(%) 또는 누적 손익 지표
    private double avgPnl;     // 평균 수익률(%)
    private double maxPnl;     // 최대 이익(%)
    private double minPnl;     // 최대 손실(%)
}
