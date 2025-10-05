package com.goldSys.BE.trade.controller;

import com.goldSys.BE.trade.dto.TradeRequestDto;
import com.goldSys.BE.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/record")
    public ResponseEntity<?> recordTrade(@RequestBody TradeRequestDto dto) {
        try {
            tradeService.recordTrade(dto);
            return ResponseEntity.ok("거래 저장 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("거래 저장 실패: " + e.getMessage());
        }
    }
}
