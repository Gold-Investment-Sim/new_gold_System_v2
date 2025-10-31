package com.goldSys.BE.history.dto;

import com.goldSys.BE.history.entity.TradeHistoryRow;
import com.goldSys.BE.trade.entity.SimulationTrade;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SimulationHistoryDto {
    private Long id;           // 거래 번호
    private String date;       // yyyy-MM-dd
    private String type;       // 매수 / 매도
    private String goldPrice;  // 금 시세(원/g)
    private String quantity;   // 거래 수량(g)
    private String amount;     // 거래 금액(원)
    private Double pnl;        // 수익률(%)
    private String result;     // 손익 | 손실 | 미풀이
    private String note;       // trade에는 없음 → null

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

        this.note = null;
    }
}
