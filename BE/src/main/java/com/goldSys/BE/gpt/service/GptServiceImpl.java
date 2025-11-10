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
        String systemPrompt = "당신은 10년 경력의 베테랑 금 투자 전문가 '골드맨'입니다. " +
                "제공된 [오늘의 빅 뉴스]와 [사용자 거래 내역]을 바탕으로 " +
                "초보 투자자에게 **명확하고 직설적인 조언**을 해주세요. " +
                "1. [오늘의 빅 뉴스]가 금 시세에 어떤 영향을 미치는지 핵심을 짚어주세요. " +
                "(예: '계엄령 선포는 심각한 사회 불안을 야기하여 안전 자산인 금 수요를 폭증시킵니다.') " +
                "2. 이 뉴스를 바탕으로 [사용자 거래 내역](매수/매도)이 '매우 현명한 선택'이었는지, '치명적인 실수'였는지 **명확하게 O/X로 평가**하고, " +
                "그 이유를 설명해주세요. " +
                "**'~일 수 있습니다' 같은 애매한 표현은 절대 금지입니다.**";
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