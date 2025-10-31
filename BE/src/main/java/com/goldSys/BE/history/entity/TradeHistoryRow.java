package com.goldSys.BE.history.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "TradeHistoryRow")      // 엔티티 이름만 다르면 동일 테이블 중복매핑 가능
@Table(name = "simulation_trade")
@Immutable                              // 읽기 전용
@Getter @NoArgsConstructor
public class TradeHistoryRow {

    @Id
    @Column(name = "TRADE_NO")
    private Long tradeNo;

    @Column(name = "MEMBER_NO", nullable = false)
    private Long memberNo;

    @Column(name = "TRADE_DATE", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "TRADE_TYPE", nullable = false, length = 10)
    private String tradeType;

    @Column(name = "GOLD_PRICE", precision = 10, scale = 2)
    private BigDecimal goldPrice;

    @Column(name = "QUANTITY", precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "PNL", precision = 6, scale = 2)
    private BigDecimal pnl;
}
