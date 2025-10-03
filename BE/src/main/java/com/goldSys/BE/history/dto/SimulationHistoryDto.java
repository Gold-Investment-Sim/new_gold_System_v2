package com.goldSys.BE.history.dto;

import com.goldSys.BE.history.entity.SimulationHistory;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SimulationHistoryDto {
    private Long id;
    private String date;     // yyyy-MM-dd
    private String type;     // "매수"/"매도"
    private String answer;   // 예측
    private String actual;   // 실제
    private String result;   // "correct" | "wrong" | "unsolved"
    private Double pnl;
    private String note;

    public SimulationHistoryDto(SimulationHistory h) {
        this.id = h.getHistoryNo();
        this.date = h.getHistoryDate() == null ? null : h.getHistoryDate().toString();
        this.type = h.getHistoryType();
        this.answer = h.getHistoryPredict();
        this.actual = h.getHistoryResult();
        this.result = h.getHistoryResult() == null
                ? "unsolved"
                : (safeEq(h.getHistoryResult(), h.getHistoryPredict()) ? "correct" : "wrong");
        this.pnl = h.getPnl();
        this.note = h.getNote();
    }
    private boolean safeEq(String a, String b) { return a != null && b != null && a.trim().equals(b.trim()); }
}