// BE/src/main/java/com/goldSys/BE/service/GeminiService.java

package com.goldSys.BE.service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeminiService {

    private final String projectId;
    private final String location;
    private final String apiKey;

    public GeminiService(
            @Value("${gemini.project-id}") String projectId,
            @Value("${gemini.location}") String location,
            @Value("${gemini.api-key}") String apiKey) {
        this.projectId = projectId;
        this.location = location;
        this.apiKey = apiKey;
    }

    /**
     * 시뮬레이션 결과에 대한 분석 피드백을 요청하는 메서드
     */
    public String getAnalysisFeedback(String simulationResult) {
        String prompt = buildPrompt(simulationResult);

        try (VertexAI vertexAI = new VertexAI(this.projectId, this.location)) {
            GenerativeModel model = new GenerativeModel("gemini-1.5-pro-preview-0409", vertexAI);
            GenerateContentResponse response = model.generateContent(prompt);
            return ResponseHandler.getText(response);
        } catch (IOException e) {
            e.printStackTrace();
            return "AI 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    /**
     * Gemini에게 보낼 질문(프롬프트)을 만드는 헬퍼 메서드
     */
    private String buildPrompt(String simulationResult) {
        return "당신은 금융 투자 전문가입니다. 사용자가 금 투자 시뮬레이션을 마쳤고, 그 결과는 다음과 같습니다.\n\n"
                + "--- 시뮬레이션 결과 ---\n"
                + simulationResult + "\n"
                + "---------------------\n\n"
                + "이 결과에 대해 초보 투자자가 이해하기 쉽게, 당시의 경제 상황과 연관 지어 친절하게 분석해주세요. "
                + "분석 내용은 2~3문장으로 요약해주세요.";
    }

    public String getApiKey() {
        return apiKey;
    }
}