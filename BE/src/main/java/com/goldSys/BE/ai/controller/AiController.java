package com.goldSys.BE.ai.controller;

import com.goldSys.BE.ai.dto.AnalysisRequestDto;
import com.goldSys.BE.ai.dto.AnalysisResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final OpenAiChatModel openAiChatModel;

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponseDto> analyzeResult(@RequestBody AnalysisRequestDto request) {

        String prompt = String.format("""
            당신은 'GoldSim' 서비스의 친절한 투자 결과 분석가입니다.
            아래에 있는 사용자의 금 투자 시뮬레이션 결과를 바탕으로, 투자 초보자도 이해하기 쉽게 분석 코멘트를 작성해주세요.
            결과는 존댓말로, 부드럽고 친근한 말투로 설명해주세요.
            수익률, 시장 상황 등 핵심적인 내용만 간결하게 포함하고, 긍정적인 격려의 말을 덧붙여주세요.

            --- 사용자의 거래 결과 ---
            %s
            ---------------------------

            위 결과를 바탕으로 분석 코멘트를 작성해주세요:
            """, request.getResultText());

        String analysisResult = openAiChatModel.call(prompt);

        return ResponseEntity.ok(new AnalysisResponseDto(analysisResult));
    }
}