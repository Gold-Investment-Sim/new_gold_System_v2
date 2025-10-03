package com.goldSys.BE.member.mail;

public enum EmailTemplate {
    SIGNUP_VERIFICATION("[GoldSim] 이메일 인증코드",
            """
            안녕하세요, GoldSim입니다.
    
            인증코드: ${code}
            유효시간: ${ttlMin}분
    
            만약 본인이 요청한 것이 아니라면 본 메일을 무시하세요.
            """,
            false),

    FORGOT_PASSWORD("[GoldSim] 임시 비밀번호 안내",
            """
            안녕하세요, GoldSim입니다.
    
            임시 비밀번호: ${tempPassword}
            로그인 후 반드시 비밀번호를 변경하세요.
            """,
            false);

    public final String subject;
    public final String body;   // ${var} 치환
    public final boolean html;
    EmailTemplate(String s, String b, boolean html){ this.subject=s; this.body=b; this.html=html; }
}