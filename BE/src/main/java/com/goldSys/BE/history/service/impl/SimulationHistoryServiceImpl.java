package com.goldSys.BE.history.service.impl;

import com.goldSys.BE.history.dto.HistoryListDto;
import com.goldSys.BE.history.dto.HistoryStatsDto;
import com.goldSys.BE.history.dto.HistorySummaryDto;
import com.goldSys.BE.history.dto.SimulationHistoryDto;
import com.goldSys.BE.history.entity.SimulationHistory;
import com.goldSys.BE.history.repository.HistoryStatsRepository;
import com.goldSys.BE.history.repository.SimulationHistoryRepository;
import com.goldSys.BE.history.service.SimulationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SimulationHistoryServiceImpl implements SimulationHistoryService {

    private final SimulationHistoryRepository repo;
    private final HistoryStatsRepository statsRepo;

    @Override
    public HistoryListDto getHistory(Long memberNo, LocalDate from, LocalDate to,
                                     String type, String sort, int page, int size) {
        int p = Math.max(1, page);
        int s = Math.min(Math.max(1, size), 100);

        Sort order = "date,asc".equalsIgnoreCase(String.valueOf(sort))
                ? Sort.by(Sort.Direction.ASC, "historyDate").and(Sort.by(Sort.Direction.ASC, "historyNo"))
                : Sort.by(Sort.Direction.DESC, "historyDate").and(Sort.by(Sort.Direction.DESC, "historyNo"));

        Pageable pageable = PageRequest.of(p - 1, s, order);

        Specification<SimulationHistory> spec = Specification
                .where(eqMember(memberNo))
                .and(betweenDate(from, to))
                .and(eqType(type));

        Page<SimulationHistory> pg = repo.findAll(spec, pageable);

        List<SimulationHistoryDto> items = pg.getContent().stream()
                .map(SimulationHistoryDto::new)
                .toList();

        return new HistoryListDto(items, p, s, pg.getTotalElements());
    }

    private Specification<SimulationHistory> eqMember(Long memberNo) {
        return (root, q, cb) -> cb.equal(root.get("memberNo"), memberNo);
    }
    private Specification<SimulationHistory> betweenDate(LocalDate from, LocalDate to) {
        return (root, q, cb) -> cb.between(root.get("historyDate"), from, to);
    }
    private Specification<SimulationHistory> eqType(String type) {
        if (type == null || type.isBlank()) return (r, q, cb) -> cb.conjunction();
        return (root, q, cb) -> cb.equal(root.get("historyType"), type);
    }

    @Override
    public HistoryStatsDto getHistoryStats(Long memberNo, String from, String to, String type) {
        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);

        HistoryStatsRepository.StatsAgg a = statsRepo.aggregateStats(memberNo, f, t, type);
        long total   = a == null ? 0 : a.getTotal();      // 날짜 수(QUOTES_DAILY)
        long correct = a == null ? 0 : a.getCorrect();
        long wrong   = a == null ? 0 : a.getWrong();
        long unsolved= Math.max(0, total - correct - wrong);
        double acc   = (correct + wrong) > 0 ? (double) correct / (correct + wrong) : 0.0;

        return new HistoryStatsDto(total, correct, wrong, unsolved, acc);
    }

    @Override
    public HistorySummaryDto getHistorySummary(Long memberNo) {
        SimulationHistoryRepository.SummaryAgg a = repo.aggregateSummaryByMember(memberNo);

        long total   = a == null ? 0 : a.getTotal();
        long correct = a == null ? 0 : n(a.getCorrect());
        long wrong   = a == null ? 0 : n(a.getWrong());
        long unsolved= Math.max(0, total - correct - wrong);

        double totalPnl = a == null ? 0.0 : d(a.getTotalPnl());
        double avgPnl   = a == null ? 0.0 : d(a.getAvgPnl());
        double maxPnl   = a == null ? 0.0 : d(a.getMaxPnl());
        double minPnl   = a == null ? 0.0 : d(a.getMinPnl());

        double acc = (correct + wrong) > 0 ? (double) correct / (correct + wrong) : 0.0;

        return new HistorySummaryDto(total, correct, wrong, unsolved,
                totalPnl, avgPnl, maxPnl, minPnl, acc);
    }

    private long n(Long v) { return v == null ? 0L : v; }
    private double d(Double v) { return v == null ? 0.0 : v; }
}