package com.goldSys.BE.member.service;

public interface EmailVerificationService {
    void sendCode(String email);
    boolean verifyCode(String email, String code);
}