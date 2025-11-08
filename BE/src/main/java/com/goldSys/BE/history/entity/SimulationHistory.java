package com.goldSys.BE.history.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 테이블 변동으로 사용 안함
 * 개발자 : 최승희
 * 투자 이력 테이블 매핑 Entity
 * 테이블명 : SIMULATION_HISTORY
 * 사용자의 가상 투자 내역(예측 결과, 실제 결과, 손익 등)을 기록
 * 프론트의 투자이력(History) 기능과 직접 연결됨
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SIMULATION_HISTORY")
public class SimulationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_NO")
    private Long historyNo;

    @Column(name = "MEMBER_NO", nullable = false)
    private Long memberNo;

    @Column(name = "HISTORY_DATE", nullable = false)
    private LocalDate historyDate;          // DB 컬럼이 DATE라고 가정

    @Column(name = "HISTORY_TYPE", length = 10, nullable = false) // "매수"/"매도"
    private String historyType;

    @Column(name = "HISTORY_PREDICT")
    private String historyPredict;

    @Column(name = "HISTORY_RESULT")
    private String historyResult;

    @Column(name = "PNL")
    private Double pnl;

    @Column(name = "FAVORITE")
    private Boolean favorite;

    @Column(name = "TAGS")
    private String tags;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}