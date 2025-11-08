package com.goldSys.BE.metrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

/**
 * 개발자 : 최승희
 * 메트릭 시계열 포인트 DTO
 * 구성 : 날짜(date) + 값(value)
 * 사용 : 메트릭 시계열 조회(/api/metrics/series) 응답 데이터 구조
 */
@Data
@AllArgsConstructor
public class SeriesPointDto {
    private LocalDate date;

    private Double value;
}
