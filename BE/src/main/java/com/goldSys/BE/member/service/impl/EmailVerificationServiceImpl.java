// src/main/java/com/shinhan/backend/member/service/impl/EmailVerificationServiceImpl.java
package com.goldSys.BE.member.service.impl;

import com.goldSys.BE.member.mail.EmailTemplate;
import com.goldSys.BE.member.mail.Mailer;
import com.goldSys.BE.member.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private static final int TTL_MIN = 3;
    private static final int RESEND_COOLDOWN_SEC = 10;

    private final Mailer mailer;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom rnd = new SecureRandom();
    private final Map<String, Item> store = new ConcurrentHashMap<>();

    private static class Item {
        final String hash; final LocalDateTime exp; final LocalDateTime sent; final boolean verified;
        Item(String h, LocalDateTime e, LocalDateTime s, boolean v){ this.hash=h; this.exp=e; this.sent=s; this.verified=v; }
    }

    @Override
    public void sendCode(String emailRaw) {
        String email = normalize(emailRaw);
        if (email.isEmpty()) return;
        Item p = store.get(email);
        if (p != null && p.sent.plusSeconds(RESEND_COOLDOWN_SEC).isAfter(LocalDateTime.now())) return;

        String code = String.format("%06d", rnd.nextInt(1_000_000));
        String hash = passwordEncoder.encode(code);
        LocalDateTime now = LocalDateTime.now();
        store.put(email, new Item(hash, now.plusMinutes(TTL_MIN), now, false));

        mailer.sendTemplate(email, EmailTemplate.SIGNUP_VERIFICATION,
                Map.of("code", code, "ttlMin", TTL_MIN));
    }

    @Override
    public boolean verifyCode(String emailRaw, String code) {
        String email = normalize(emailRaw);
        if (email.isEmpty() || code == null || code.isBlank()) return false;
        Item it = store.get(email);
        if (it == null || it.verified || LocalDateTime.now().isAfter(it.exp)) return false;

        boolean ok = passwordEncoder.matches(code, it.hash);
        if (ok) store.put(email, new Item(it.hash, it.exp, it.sent, true));
        return ok;
    }

    @Scheduled(fixedRate = 300_000)
    void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        store.entrySet().removeIf(e -> now.isAfter(e.getValue().exp));
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
