package com.goldSys.BE.history.controller;

import com.goldSys.BE.history.dto.HistoryListDto;
import com.goldSys.BE.history.dto.HistoryStatsDto;
import com.goldSys.BE.history.dto.HistorySummaryDto;
import com.goldSys.BE.history.service.SimulationHistoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class SimulationHistoryController {

    private final SimulationHistoryService service;

    @GetMapping
    public HistoryListDto getHistory(
            @RequestParam(defaultValue = "2023-01-01") String from,
            @RequestParam(defaultValue = "2024-12-31") String to,
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "date,desc") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session
    ) {
        Object uno = session.getAttribute("LOGIN_NO");
        if (uno == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
        Long memberNo = Long.valueOf(uno.toString());

        LocalDate f = LocalDate.parse(from);
        LocalDate t = LocalDate.parse(to);
        if (t.isBefore(f)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "날짜 범위 오류");

        return service.getHistory(memberNo, f, t, type, sort, page, size);
    }

    @GetMapping("/stats")
    public HistoryStatsDto getStats(@RequestParam String from,
                                    @RequestParam String to,
                                    @RequestParam(defaultValue = "") String type,
                                    HttpSession session) {
        Object uno = session.getAttribute("LOGIN_NO");
        if (uno == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Long memberNo = Long.valueOf(uno.toString());
        return service.getHistoryStats(memberNo, from, to, type);
    }

    @GetMapping("/summary")
    public HistorySummaryDto getSummary(HttpSession session) {
        Object uno = session.getAttribute("LOGIN_NO");
        if (uno == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Long memberNo = Long.valueOf(uno.toString());
        return service.getHistorySummary(memberNo);
    }
}