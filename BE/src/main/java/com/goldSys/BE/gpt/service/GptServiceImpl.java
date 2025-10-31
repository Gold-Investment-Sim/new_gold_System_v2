package com.goldSys.BE.gpt.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GptServiceImpl implements GptService {

    private final ChatClient chatClient;

    // build.gradle에 추가한 Spring AI 라이브러 덕분에,
    // Spring이 ChatClient.Builder를 자동으로 만들어줍니다. 우리는 주입받아 사용하기만 하면 됩니다.
    public GptServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generateAnalysis(String resultText) {
        // GPT에게 어떤 역할을 수행할지 알려주는 부분입니다. (가장 중요!)
        String systemPrompt =  "당신은 금 투자 전문가 입니다. 이날 왜 이런 금값 동향이 일어났는지 전날의 빅 뉴스를 바탕으로 분석/요약해줘. 초보자들이 알기 쉽게 2~3문장으로 요약해줘";

        try {
            // Spring AI의 ChatClient를 사용하여 API 호출
            return chatClient.prompt()
                    .system(systemPrompt) // 1. AI의 역할 부여
                    .user(resultText)     // 2. 사용자가 보낸 분석할 텍스트 전달
                    .call()               // 3. GPT API 호출
                    .content();           // 4. 응답 내용(content)만 추출하여 반환

        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 에러 기록
            return "AI 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}