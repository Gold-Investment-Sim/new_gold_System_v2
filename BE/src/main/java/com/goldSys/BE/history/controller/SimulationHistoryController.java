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

/**
 * 개발자 : 최승희
 * 투자 이력 관련 컨트롤러
 * 엔드포인트: /api/history
 * 메서드 기능
 * getHistory : 투자 이력 목록 조회 (페이징, 정렬)
 * getStats   : 투자 통계 조회 (손익/손실 비율 등)
 * getSummary : 투자 성과 요약 조회 (기간별 요약)
 * 세션 정보(LOGIN_NO)를 통해 회원 식별
 */
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class SimulationHistoryController {

    private final SimulationHistoryService service; // 서비스 호출

    /**
     * 개발자 : 최승희
     * 투자 이력 목록 조회 기능
     * @param from    조회 시작일 (기본값: 2023-01-01)
     * @param to      조회 종료일 (기본값: 2024-12-31)
     * @param type    거래 유형 필터 ("매수"/"매도"), 기본값은 전체
     * @param sort    정렬 기준 (예: "date,desc")
     * @param page    페이지 번호 (1-base)
     * @param size    페이지당 데이터 개수
     * @param session 로그인 세션 (LOGIN_NO 포함)
     * @return        HistoryListDto + SimulationHistoryDto (투자 이력 리스트 + 페이징 정보)
     */
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

        // 회원번호, 기간, 필터, 정렬, 페이징을 기준으로 서비스에서 데이터 조회
        return service.getHistory(memberNo, f, t, type, sort, page, size);
    }

    /**
     * 개발자 : 최승희
     * 투자 통계 조회 기능
     * @param from    조회 시작일
     * @param to      조회 종료일
     * @param type    거래 유형 필터 ("매수"/"매도"), 기본값은 전체
     * @param session 로그인 세션
     * @return        HistoryStatsDto (총 거래수, 손익수, 손실수 등 통계 정보)
     */
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

    /**
     * 개발자 : 최승희
     * 투자 성과 요약 조회 기능
     * @param session 로그인 세션
     * @param from    조회 시작일
     * @param to      조회 종료일
     * @param type    거래 유형 필터(전체)
     * @return        HistorySummaryDto (기간별 총합, 수익률 등 요약 데이터)
     */
    @GetMapping("/summary")
    public HistorySummaryDto getSummary(HttpSession session,
                                        @RequestParam String from,
                                        @RequestParam String to,
                                        @RequestParam(defaultValue = "") String type) {
        Object uno = session.getAttribute("LOGIN_NO");
        if (uno == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Long memberNo = Long.valueOf(uno.toString());
        return service.getHistorySummary(memberNo, from, to);
    }
}