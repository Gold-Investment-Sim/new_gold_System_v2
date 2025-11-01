package com.goldSys.BE.trade.entity;

import com.goldSys.BE.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "simulation_trade")
public class SimulationTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRADE_NO")
    private Long tradeNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO", nullable = false)
    private Member member;

    @Column(name = "TRADE_DATE", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "TRADE_TYPE", nullable = false, length = 10)
    private String tradeType; // "매수" 또는 "매도"

    @Column(name = "GOLD_PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal goldPrice;

    @Column(name = "QUANTITY", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // ✅ 추가: 거래 손익 (Profit and Loss)
    @Column(name = "PNL", precision = 15, scale = 2)
    private BigDecimal pnl;

    @Column(name = "CREATED_AT", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp createdAt;
}
