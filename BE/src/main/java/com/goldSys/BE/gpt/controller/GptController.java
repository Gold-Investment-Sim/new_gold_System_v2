package com.goldSys.BE.gpt.controller;

import com.goldSys.BE.gpt.dto.AnalyzeRequestDto;
import com.goldSys.BE.gpt.dto.AnalyzeResponseDto;
import com.goldSys.BE.gpt.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gpt") // 이 API의 기본 주소는 "/api/gpt" 입니다.
@RequiredArgsConstructor
public class GptController {

    private final GptService gptService;

    @PostMapping("/analyze") // 최종 경로는 "/api/gpt/analyze"가 됩니다.
    public ResponseEntity<AnalyzeResponseDto> analyzeResult(@RequestBody AnalyzeRequestDto requestDto) {
        // 1. 서비스에 분석 요청을 보내고 결과를 받습니다.
        String analysisResult = gptService.generateAnalysis(requestDto.getResultText());

        // 2. 받은 결과를 DTO에 담아 프론트엔드로 보냅니다.
        return ResponseEntity.ok(new AnalyzeResponseDto(analysisResult));
    }
}