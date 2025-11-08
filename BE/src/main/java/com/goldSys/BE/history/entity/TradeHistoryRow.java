package com.goldSys.BE.history.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 개발자 : 최승희
 * 거래 이력 테이블 매핑 Entity (읽기 전용)
 * 테이블명 : SIMULATION_TRADE
 * 사용자별 가상 거래 내역(매수·매도, 수량, 금액, 손익)을 저장
 * 투자이력 분석 및 통계용으로 활용되며, 수정 불가
 */
@Entity(name = "TradeHistoryRow")      // SimulationTrade로 이미 존재, 엔티티 이름만 다르면 동일 테이블 중복매핑 가능
@Table(name = "simulation_trade")
@Immutable                              // 읽기 전용 엔티티
@Getter @NoArgsConstructor
public class TradeHistoryRow {

    @Id
    @Column(name = "TRADE_NO")
    private Long tradeNo; // 거래 고유번호 (PK)

    @Column(name = "MEMBER_NO", nullable = false)
    private Long memberNo; // 회원 고유번호 (FK)

    @Column(name = "TRADE_DATE", nullable = false)
    private LocalDate tradeDate; // 거래 일자 (yyyy-MM-dd)

    @Column(name = "TRADE_TYPE", nullable = false, length = 10)
    private String tradeType; // 거래 유형 ("매수" / "매도")

    @Column(name = "GOLD_PRICE", precision = 10, scale = 2)
    private BigDecimal goldPrice; // 거래 단가 (금 1g당 가격, KRW 기준)

    @Column(name = "QUANTITY", precision = 10, scale = 3)
    private BigDecimal quantity; // 거래 수량 (g 단위)

    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount; // 거래 총액 (단가 × 수량, KRW)

    @Column(name = "PNL", precision = 6, scale = 2)
    private BigDecimal pnl; // 손익 (Profit and Loss, +수익 / -손실)
}
