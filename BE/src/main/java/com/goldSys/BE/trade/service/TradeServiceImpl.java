package com.goldSys.BE.trade.service;

import com.goldSys.BE.member.entity.Member;
import com.goldSys.BE.member.repository.MemberRepository;
import com.goldSys.BE.member.repository.MemberAssetRepository;
import com.goldSys.BE.trade.dto.TradeRequestDto;
import com.goldSys.BE.trade.entity.SimulationTrade;
import com.goldSys.BE.trade.repository.SimulationTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final SimulationTradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final MemberAssetRepository memberAssetRepository;

    /**
     * 거래 기록 저장 + 자산 업데이트 + 최근 매수가 기준 ROI 계산
     */
    @Override
    @Transactional
    public Map<String, Object> recordTrade(TradeRequestDto dto) {

        // ✅ 1. 회원 존재 확인
        Member member = memberRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        BigDecimal buyValue = BigDecimal.ZERO;
        BigDecimal sellValue = BigDecimal.ZERO;
        BigDecimal pnl = BigDecimal.ZERO; // ROI(%)

        // ✅ 2. 거래 구분별 처리
        if ("매수".equals(dto.getTradeType())) {
            buyValue = dto.getGoldPrice().multiply(dto.getQuantity());

        } else if ("매도".equals(dto.getTradeType())) {
            sellValue = dto.getGoldPrice().multiply(dto.getQuantity());

            // ✅ 회원의 최근 매수 1건 가져오기
            List<SimulationTrade> history = tradeRepository.findAll();
            SimulationTrade recentBuy = history.stream()
                    .filter(t -> t.getMember().getMemberNo().equals(dto.getMemberNo()))
                    .filter(t -> "매수".equals(t.getTradeType()))
                    .reduce((first, second) -> second) // 마지막 매수만 남김
                    .orElse(null);

            // ✅ ROI 계산 (최근 매수 1건 기준)
            if (recentBuy != null && recentBuy.getGoldPrice().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal buyPrice = recentBuy.getGoldPrice();
                pnl = dto.getGoldPrice()
                        .subtract(buyPrice)
                        .divide(buyPrice, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }

        // ✅ 3. 잔액 갱신
        BigDecimal currentBalance = dto.getCurrentBalance() != null
                ? dto.getCurrentBalance()
                : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.subtract(buyValue).add(sellValue);

        // ✅ 4. 거래 저장
        SimulationTrade trade = SimulationTrade.builder()
                .member(member)
                .tradeDate(LocalDate.now())
                .tradeType(dto.getTradeType())
                .goldPrice(dto.getGoldPrice())
                .quantity(dto.getQuantity())
                .amount(dto.getGoldPrice().multiply(dto.getQuantity()))
                .build();

        trade.setPnl(pnl);
        tradeRepository.save(trade);

        // ✅ 5. 보유 금량 계산 + 잔액 업데이트
        BigDecimal ownedGold = tradeRepository.findTotalGoldByMemberNo(dto.getMemberNo());
        memberAssetRepository.updateBalance(dto.getMemberNo(), newBalance);

        // ✅ 6. 결과 반환
        Map<String, Object> result = new HashMap<>();
        result.put("message", "거래 성공");
        result.put("newBalance", newBalance);
        result.put("ownedGold", ownedGold);
        result.put("pnl", pnl);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getOwnedGold(Long memberNo) {
        return tradeRepository.findTotalGoldByMemberNo(memberNo);
    }
}
