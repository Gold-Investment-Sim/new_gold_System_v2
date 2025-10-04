package com.goldSys.BE.trade.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TradeRequestDto {
    private Long memberNo;
    private String tradeType;     // "매수" / "매도"
    private BigDecimal goldPrice; // 금 시세 (원/g)
    private BigDecimal quantity;  // 거래 수량 (g)
    private String predict;       // 예측 정보 (선택)
}
