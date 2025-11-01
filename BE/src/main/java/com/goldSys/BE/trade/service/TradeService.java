package com.goldSys.BE.trade.service;

import com.goldSys.BE.trade.dto.TradeRequestDto;
import java.math.BigDecimal;
import java.util.Map;

public interface TradeService {

    /**
     * 거래 기록 저장 후 자산/보유금 업데이트 반환
     */
    Map<String, Object> recordTrade(TradeRequestDto dto);

    /**
     * 회원별 현재 보유 금(g) 조회
     */
    BigDecimal getOwnedGold(Long memberNo);
}
