package com.goldSys.BE.history.dto;

import com.goldSys.BE.history.entity.TradeHistoryRow;
import lombok.*;

/**
 * 개발자 : 최승희
 * 투자 이력 단일 항목 DTO
 * 구성 : 거래 기본정보 + 손익 정보
 * 사용 : 투자이력 목록 조회 (/api/history)
 * 매핑 대상 : TradeHistoryRow (simulation_trade 테이블 읽기 전용 엔티티)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationHistoryDto {
    private Long id;          // 거래 번호 (TRADE_NO)
    private String date;      // 거래 일자 (yyyy-MM-dd)
    private String type;      // 거래 유형 ("매수" / "매도")
    private String goldPrice; // 거래 단가 (금 1g당 원화)
    private String quantity;  // 거래 수량 (g)
    private String amount;    // 거래 총액 (원)
    private Double pnl;       // 손익률 (%)
    private String result;    // 거래 결과 ("손익" / "손실" / "미풀이")

    /**
     * TradeHistoryRow 엔티티를 기반으로 DTO 초기화
     * BigDecimal → String 변환 시 null 안전 처리
     * PNL 값의 부호에 따라 거래 결과(result) 지정
     */
    public SimulationHistoryDto(TradeHistoryRow r) {
        this.id = r.getTradeNo();
        this.date = r.getTradeDate() == null ? null : r.getTradeDate().toString();
        this.type = r.getTradeType();
        this.goldPrice = r.getGoldPrice() == null ? null : r.getGoldPrice().toPlainString();
        this.quantity  = r.getQuantity()  == null ? null : r.getQuantity().toPlainString();  // ✅ 추가
        this.amount    = r.getAmount()    == null ? null : r.getAmount().toPlainString();
        this.pnl = r.getPnl() == null ? null : r.getPnl().doubleValue();

        if (r.getPnl() == null) this.result = "미풀이";
        else if (r.getPnl().signum() > 0) this.result = "손익";
        else if (r.getPnl().signum() < 0) this.result = "손실";
        else this.result = "미풀이";
    }
}
