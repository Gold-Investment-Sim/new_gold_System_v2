package com.goldSys.BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 허용 (WebConfig에서 설정)
                .cors(Customizer.withDefaults())
                // SPA 환경에서 CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 세션 기반 로그인 유지
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // 기본 폼/팝업 비활성화
                .formLogin(f -> f.disable())
                .httpBasic(h -> h.disable())
                // 인증 실패 시 401 반환
                .exceptionHandling(e ->
                        e.authenticationEntryPoint((req, res, ex) -> res.sendError(401))
                )
                // 요청별 권한 규칙
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 공개 API
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/news/**").permitAll()
                        .requestMatchers("/api/metrics/**").permitAll()
                        .requestMatchers("/api/simulation/**").permitAll()
                        .requestMatchers("/api/history/**").permitAll()
                        // 그 밖의 모든 요청
                        .anyRequest().authenticated()
                )
                // 로그아웃 처리
                .logout(l -> l
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler((req, res, auth) -> {
                            var session = req.getSession(false);
                            if (session != null) session.invalidate();
                        })
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
