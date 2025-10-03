package com.goldSys.BE.history.service;

import com.goldSys.BE.history.dto.HistoryListDto;
import com.goldSys.BE.history.dto.HistoryStatsDto;
import com.goldSys.BE.history.dto.HistorySummaryDto;

import java.time.LocalDate;

public interface SimulationHistoryService {
    HistoryListDto getHistory(Long memberNo, LocalDate from, LocalDate to,
                              String type, String sort, int page, int size);

    HistoryStatsDto getHistoryStats(Long memberNo, String from, String to, String type);

    HistorySummaryDto getHistorySummary(Long memberNo);

}