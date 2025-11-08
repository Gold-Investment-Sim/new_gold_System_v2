package com.goldSys.BE.history.service;

import com.goldSys.BE.history.dto.HistoryListDto;
import com.goldSys.BE.history.dto.HistoryStatsDto;
import com.goldSys.BE.history.dto.HistorySummaryDto;

import java.time.LocalDate;

/**
 * 개발자 : 최승희
 * 투자 이력 서비스 인터페이스
 *
 * 주요 기능
 * 1. getHistory        : 기간·유형·정렬 조건으로 투자 이력 목록 조회
 * 2. getHistoryStats   : 기간별 정답/오답 통계 조회
 * 3. getHistorySummary : 기간별 거래 성과 요약 조회
 *
 * 사용
 * SimulationHistoryController
 */
public interface SimulationHistoryService {

    /**
     * 투자 이력 목록 조회
     * @param memberNo 회원 번호
     * @param from     조회 시작일
     * @param to       조회 종료일
     * @param type     거래 유형
     * @param sort     정렬 기준
     * @param page     페이지 번호
     * @param size     페이지당 데이터 개수
     * @return HistoryListDto
     */
    HistoryListDto getHistory(Long memberNo, LocalDate from, LocalDate to,
                              String type, String sort, int page, int size);

    /**
     * 투자 통계 조회
     * @param memberNo 회원 번호
     * @param from     조회 시작일
     * @param to       조회 종료일
     * @param type     거래 유형
     * @return HistoryStatsDto
     */
    HistoryStatsDto getHistoryStats(Long memberNo, String from, String to, String type);

    /**
     * 투자 성과 요약 조회
     * @param memberNo 회원 번호
     * @param from     조회 시작일
     * @param to       조회 종료일
     * @return HistorySummaryDto
     */
    HistorySummaryDto getHistorySummary(Long memberNo, String from, String to);

}