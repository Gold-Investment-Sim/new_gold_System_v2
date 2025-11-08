package com.goldSys.BE.history.service.impl;

import com.goldSys.BE.history.dto.HistoryListDto;
import com.goldSys.BE.history.dto.HistoryStatsDto;
import com.goldSys.BE.history.dto.HistorySummaryDto;
import com.goldSys.BE.history.dto.SimulationHistoryDto;
import com.goldSys.BE.history.entity.TradeHistoryRow;
import com.goldSys.BE.history.repository.HistoryStatsRepository;
import com.goldSys.BE.history.repository.HistorySummaryRepository;
import com.goldSys.BE.history.repository.TradeHistoryRowRepository;
import com.goldSys.BE.history.service.SimulationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 개발자 : 최승희
 * 투자 이력 서비스 구현체
 *
 * 역할
 * 1. 투자 이력 목록 조회(페이징, 정렬, 필터)
 * 2. 통계(손익, 손실, 미풀이, 정혹도) 계산
 * 3. 성과 요약(매수/매도, 평균금액, 손익률 통계) 계산
 */
@Service
@RequiredArgsConstructor
public class SimulationHistoryServiceImpl implements SimulationHistoryService {

    private final TradeHistoryRowRepository repo;       // 투자 이력 목록 조회
    private final HistoryStatsRepository statsRepo;     // 투자 손익/손실 통계
    private final HistorySummaryRepository summaryRepo; // 성과 요약

    /**
     * 투자 이력 목록 조회
     * @param memberNo 회원 번호
     * @param from     조회 시작일
     * @param to       조회 종료일
     * @param type     거래 유형
     * @param sort     정렬 기준
     * @param page     페이지 번호
     * @param size     페이지당 데이터 개수
     * @return
     */
    @Override
    public HistoryListDto getHistory(Long memberNo, LocalDate from, LocalDate to,
                                     String type, String sort, int page, int size) {

        int p = Math.max(1, page); // 최소 페이지
        int s = Math.min(Math.max(1, size), 100); // 페이지 크기 제한

        // 정렬 기준 => 기본은 날짜 내림차순
        Sort order;
        if ("date,asc".equalsIgnoreCase(sort)) {
            order = Sort.by(Sort.Direction.ASC, "tradeDate")
                    .and(Sort.by(Sort.Direction.ASC, "tradeNo"));
        } else {
            order = Sort.by(Sort.Direction.DESC, "tradeDate")
                    .and(Sort.by(Sort.Direction.DESC, "tradeNo"));
        }

        Pageable pageable = PageRequest.of(p - 1, s, order);

        // 검색 조건 설정
        Specification<TradeHistoryRow> spec = Specification.allOf(
                filterByMember(memberNo),
                betweenDate(from, to),
                filterByTradeType(type)
        );

        // 데이터 조회
        Page<TradeHistoryRow> pg = repo.findAll(spec, pageable);

        List<SimulationHistoryDto> items = pg.getContent().stream()
                .map(SimulationHistoryDto::new)
                .toList();

        return new HistoryListDto(items, p, s, pg.getTotalElements());
    }

    private Specification<TradeHistoryRow> filterByMember(Long memberNo) {
        return (root, q, cb) -> cb.equal(root.get("memberNo"), memberNo);
    }

    private Specification<TradeHistoryRow> betweenDate(LocalDate from, LocalDate to) {
        return (root, q, cb) -> cb.between(root.get("tradeDate"), from, to);
    }

    private Specification<TradeHistoryRow> filterByTradeType(String type) {
        if (type == null || type.isBlank()) return null; // null 허용됨
        return (root, q, cb) -> cb.equal(root.get("tradeType"), type);
    }

    /**
     * 투자 통계 조회
     * @param memberNo 회원 번호
     * @param from     조회 시작일
     * @param to       조회 종료일
     * @param type     거래 유형
     * @return
     */
    @Override
    public HistoryStatsDto getHistoryStats(Long memberNo, String from, String to, String type) {
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);

        HistoryStatsRepository.StatsAgg a = statsRepo.aggregateStats(memberNo, f, t, type);
        long total    = a == null ? 0 : a.getTotal();
        long correct  = a == null ? 0 : a.getCorrect();
        long wrong    = a == null ? 0 : a.getWrong();
        long unsolved = Math.max(0, total - correct - wrong);
        double acc    = (correct + wrong) > 0 ? (double) correct / (correct + wrong) : 0.0;


        return new HistoryStatsDto(total, correct, wrong, unsolved, acc);
    }

    /**
     * 투자 성과 요약 조회
     * @param memberNo 회원 번호
     * @param from     조회 시작일
     * @param to       조회 종료일
     * @return
     */
    @Override
    public HistorySummaryDto getHistorySummary(Long memberNo, String from, String to) {
        var f = LocalDate.parse(from);
        var t = LocalDate.parse(to);

        var a = summaryRepo.aggregate(memberNo, f, t);
        if (a == null) return new HistorySummaryDto(0,0,0,0,0,0,0,0);

        long buy  = a.getBuy()  == null ? 0L : a.getBuy();
        long sell = a.getSell() == null ? 0L : a.getSell();

        return new HistorySummaryDto(
                a.getTotal(),
                buy,
                sell,
                ifNullToZero(a.getAvgAmount()),
                ifNullToZero(a.getTotalPnl()),
                ifNullToZero(a.getAvgPnl()),
                ifNullToZero(a.getMaxPnl()),
                ifNullToZero(a.getMinPnl())
        );
    }

    private double ifNullToZero(Double v) { return v == null ? 0.0 : v; }
}