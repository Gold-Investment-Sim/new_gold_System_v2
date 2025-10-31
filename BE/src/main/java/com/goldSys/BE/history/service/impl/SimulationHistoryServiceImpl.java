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



// Service
@Service
@RequiredArgsConstructor
public class SimulationHistoryServiceImpl implements SimulationHistoryService {

    private final TradeHistoryRowRepository repo; // <- history 전용 레포
    private final HistoryStatsRepository statsRepo;
    private final HistorySummaryRepository summaryRepo;

    @Override
    public HistoryListDto getHistory(Long memberNo, LocalDate from, LocalDate to,
                                     String type, String sort, int page, int size) {
        int p = Math.max(1, page);
        int s = Math.min(Math.max(1, size), 100);

        Sort order = "date,asc".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.ASC, "tradeDate").and(Sort.by(Sort.Direction.ASC, "tradeNo"))
                : Sort.by(Sort.Direction.DESC, "tradeDate").and(Sort.by(Sort.Direction.DESC, "tradeNo"));
        Pageable pageable = PageRequest.of(p - 1, s, order);

        Specification<TradeHistoryRow> spec = Specification.allOf(
                eqMember(memberNo),
                betweenDate(from, to),
                eqType(type)
        );


        Page<TradeHistoryRow> pg = repo.findAll(spec, pageable); // <- 제네릭 일치

        List<SimulationHistoryDto> items = pg.getContent().stream()
                .map(SimulationHistoryDto::new)       // TradeHistoryRow 생성자 사용
                .toList();

        return new HistoryListDto(items, p, s, pg.getTotalElements());
    }

    // === Specs (모두 TradeHistoryRow 기준) ============================
    private Specification<TradeHistoryRow> eqMember(Long memberNo) {
        return (root, q, cb) -> cb.equal(root.get("memberNo"), memberNo);
    }

    private Specification<TradeHistoryRow> betweenDate(LocalDate from, LocalDate to) {
        return (root, q, cb) -> cb.between(root.get("tradeDate"), from, to);
    }

    private Specification<TradeHistoryRow> eqType(String type) {
        if (type == null || type.isBlank()) return null; // null 허용됨
        return (root, q, cb) -> cb.equal(root.get("tradeType"), type);
    }

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
                nz(a.getAvgAmount()),
                nz(a.getTotalPnl()),
                nz(a.getAvgPnl()),
                nz(a.getMaxPnl()),
                nz(a.getMinPnl())
        );
    }

    private double nz(Double v) { return v == null ? 0.0 : v; }


}


//
//    @Override
//    public HistorySummaryDto getHistorySummary(Long memberNo) {
//        SimulationHistoryRepository.SummaryAgg a = repo.aggregateSummaryByMember(memberNo);
//
//        long total   = a == null ? 0 : a.getTotal();
//        long correct = a == null ? 0 : n(a.getCorrect());
//        long wrong   = a == null ? 0 : n(a.getWrong());
//        long unsolved= Math.max(0, total - correct - wrong);
//
//        double totalPnl = a == null ? 0.0 : d(a.getTotalPnl());
//        double avgPnl   = a == null ? 0.0 : d(a.getAvgPnl());
//        double maxPnl   = a == null ? 0.0 : d(a.getMaxPnl());
//        double minPnl   = a == null ? 0.0 : d(a.getMinPnl());
//
//        double acc = (correct + wrong) > 0 ? (double) correct / (correct + wrong) : 0.0;
//
//        return new HistorySummaryDto(total, correct, wrong, unsolved,
//                totalPnl, avgPnl, maxPnl, minPnl, acc);
//    }
//
//    private long n(Long v) { return v == null ? 0L : v; }
//    private double d(Double v) { return v == null ? 0.0 : v; }
