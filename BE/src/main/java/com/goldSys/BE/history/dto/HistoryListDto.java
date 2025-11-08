package com.goldSys.BE.history.dto;

import lombok.*;
import java.util.List;

/**
 * 개발자 : 최승희
 * 투자 이력 목록 응답 DTO
 * 구성 : 투자 이력 리스트 + 페이징 정보
 * 사용 : 투자이력 페이지 조회 (/api/history)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryListDto {
    private List<SimulationHistoryDto> items; // 투자 이력 목록
    private int page;   // 현재 페이지 번호(1부터 시작)
    private int size;   // 페이지당 데이터 개수
    private long total; // 전체 데이터 개수
}