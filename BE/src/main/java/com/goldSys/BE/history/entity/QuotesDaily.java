package com.goldSys.BE.history.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * 개발자 : 최승희
 * 일별 시세 테이블 매핑 entity
 * 테이블명 : QUOTES_DAILY
 * 각 날짜별 금 시세, 환율, ETF 거래량, 공포지수(변동성 지수) 등의 데이터 보관
 * 투자이력 통계 및 예측 모델의 기준 데이터로 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "QUOTES_DAILY")
public class QuotesDaily {
    @Id
    @Column(name = "DATE", nullable = false)
    private LocalDate date; // 기준 일자

    @Column(name = "KRW_G_OPEN")
    private Double krwGOpen; // 금 시세 시가(원화 기준/ 1g당)

    @Column(name = "KRW_G_CLOSE")
    private Double krwGClose; // 금 시세 종가(원화 기준/ 1g당)

    @Column(name = "USD_OZ_OPEN")
    private Double usdOzOpen; // 금 시세 시가(달러 기준/ oz)

    @Column(name = "USD_OZ_CLOSE")
    private Double usdOzClose; // 금 시세 종가(달러 기준/ oz)

    @Column(name = "VIX")
    private Double vix; // 공포지수(변동성 지수) 시장의 불안도를 나타냄

    @Column(name = "ETF_VOLUME")
    private Double etfVolume; // 금 관련 ETF 거래량

    @Column(name = "FX_RATE")
    private Double fxRate; // 환율(KRW/USD)
}