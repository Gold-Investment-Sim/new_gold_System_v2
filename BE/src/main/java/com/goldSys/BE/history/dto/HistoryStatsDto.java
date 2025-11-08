package com.goldSys.BE.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 개발자 : 최승희
 * 투자 이력 통계 응답 DTO
 * 구성 : 총 거래 수, 손익/손실/미풀이 건수, 정답률
 * 사용 : 투자 통계 조회 (/api/history/stats)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryStatsDto {
    private long total;      // 전체 거래 수
    private long correct;    // 예측이 실제와 일치한 건수
    private long wrong;      // 예측이 빗나간 건수
    private long unsolved;   // 미풀이(결과 미확정) 건수
    private double accuracy; // 정답률
}