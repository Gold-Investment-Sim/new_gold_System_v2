package com.goldSys.BE.trade.controller;

import com.goldSys.BE.trade.dto.TradeRequestDto;
import com.goldSys.BE.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    /**
     * 거래 기록 저장 + 잔액 및 보유금 업데이트 반환
     */
    @PostMapping("/record")
    public ResponseEntity<?> recordTrade(@RequestBody TradeRequestDto dto) {
        try {
            Map<String, Object> result = tradeService.recordTrade(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "거래 저장 실패: " + e.getMessage()));
        }
    }

    /**
     * 현재 보유 금(g) 조회
     */
    @GetMapping("/owned/{memberNo}")
    public ResponseEntity<?> getOwnedGold(@PathVariable Long memberNo) {
        try {
            BigDecimal ownedGold = tradeService.getOwnedGold(memberNo);
            return ResponseEntity.ok(Map.of("ownedGold", ownedGold));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "보유 금량 조회 실패: " + e.getMessage()));
        }
    }
}
