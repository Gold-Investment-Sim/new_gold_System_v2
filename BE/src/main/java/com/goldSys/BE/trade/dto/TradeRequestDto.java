package com.goldSys.BE.trade.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate; // ✅ 추가

@Getter
@Setter
public class TradeRequestDto {
    private Long memberNo;           // 회원 번호
    private String tradeType;        // 매수 or 매도
    private BigDecimal goldPrice;    // 금 시세
    private BigDecimal quantity;     // 거래 수량(g)
    private BigDecimal currentBalance; // 현재 잔액
    private String predict;          // 예측 결과 (옵션)

    // ✅ 추가: 사용자가 선택한 거래 날짜
    private LocalDate tradeDate;
}