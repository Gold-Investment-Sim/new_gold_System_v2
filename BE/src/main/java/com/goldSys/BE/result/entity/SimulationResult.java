package com.goldSys.BE.result.entity;

import com.goldSys.BE.member.entity.Member;
import com.goldSys.BE.trade.entity.SimulationTrade;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "simulation_result")
public class SimulationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESULT_NO")
    private Long resultNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADE_NO", nullable = false)
    private SimulationTrade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO", nullable = false)
    private Member member;

    @Column(name = "RESULT_DATE", nullable = false)
    private LocalDate resultDate;

    @Column(name = "PNL", precision = 10, scale = 2)
    private BigDecimal pnl;

    @Column(name = "PREDICT", length = 20)
    private String predict;

    @Column(name = "ACTUAL", length = 20)
    private String actual;

    @Column(name = "FAVORITE", nullable = false)
    private Boolean favorite = false;

    @Column(name = "NOTE", columnDefinition = "TEXT")
    private String note;
}
