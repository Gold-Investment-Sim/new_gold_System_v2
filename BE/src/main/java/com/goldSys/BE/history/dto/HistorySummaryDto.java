// com.goldSys.BE.history.dto.HistorySummaryDto
package com.goldSys.BE.history.dto;

import lombok.*;

/**
 * 개발자 : 최승희
 * 투자 성과 요약 응답 DTO
 * 구성 : 거래 횟수, 매수·매도 비율, 평균·최대·최소 수익률 등
 * 사용 : 투자 성과 요약 조회 (/api/history/summary)
 * 데이터 출처 : SIMULATION_TRADE(사용), SIMULATION_HISTORY(이제 미사용) 통계 결과
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
