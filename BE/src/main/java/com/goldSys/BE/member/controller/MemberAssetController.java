package com.goldSys.BE.member.controller;

import com.goldSys.BE.member.service.MemberAssetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset")
@RequiredArgsConstructor
public class MemberAssetController {
    private final MemberAssetService assetService;
    private final Logger log = LoggerFactory.getLogger(MemberAssetController.class);

    @GetMapping("/{memberNo}")
    public ResponseEntity<?> getBalance(@PathVariable Long memberNo) {
        try {
            Long balance = assetService.getBalance(memberNo);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException ex) {
            log.info("memberNo={} 에 대한 자산 없음", memberNo);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("자산 조회 중 예외 발생, memberNo={}", memberNo, ex);
            return ResponseEntity.status(500).body("Server error: " + ex.getMessage());
        }
    }
}
