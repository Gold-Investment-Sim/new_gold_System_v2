package com.goldSys.BE.member.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Mailer {

    private final JavaMailSender sender;
    private final String fromAddr;
    private final String fromName;

    public Mailer(JavaMailSender sender,
                  @Value("${spring.mail.username}") String fromAddr,
                  @Value("${app.mail.from.name:GoldSim}") String fromName) {
        this.sender = sender;
        this.fromAddr = fromAddr;
        this.fromName = fromName;
    }

    public void sendTemplate(String to, EmailTemplate tmpl, Map<String, Object> vars) {
        try {
            String body = apply(tmpl.body, vars);

            MimeMessage mm = sender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(mm, false, StandardCharsets.UTF_8.name());
            h.setFrom(fromAddr, fromName);
            h.setTo(to);
            h.setSubject(tmpl.subject);
            h.setText(body, tmpl.html);

            sender.send(mm);
        } catch (Exception e) {
            throw new IllegalStateException("메일 전송 실패", e);
        }
    }

    // ${var} 치환 로직
    private String apply(String text, Map<String, Object> vars) {
        Matcher m = Pattern.compile("\\$\\{(.*?)}").matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String val = String.valueOf(vars.getOrDefault(key, ""));
            m.appendReplacement(sb, Matcher.quoteReplacement(val));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}