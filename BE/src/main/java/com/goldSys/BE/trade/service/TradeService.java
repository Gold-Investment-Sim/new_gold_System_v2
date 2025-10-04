package com.goldSys.BE.trade.service;

import com.goldSys.BE.trade.dto.TradeRequestDto;

public interface TradeService {
    void recordTrade(TradeRequestDto dto);
}
