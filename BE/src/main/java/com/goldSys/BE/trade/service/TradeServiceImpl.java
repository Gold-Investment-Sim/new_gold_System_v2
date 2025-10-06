package com.goldSys.BE.trade.service;

import com.goldSys.BE.member.entity.Member;
import com.goldSys.BE.member.repository.MemberAssetRepository;
import com.goldSys.BE.member.repository.MemberRepository;
import com.goldSys.BE.result.entity.SimulationResult;
import com.goldSys.BE.result.repository.SimulationResultRepository;
import com.goldSys.BE.trade.dto.TradeRequestDto;
import com.goldSys.BE.trade.entity.SimulationTrade;
import com.goldSys.BE.trade.repository.SimulationTradeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final SimulationTradeRepository tradeRepo;
    private final SimulationResultRepository resultRepo;
    private final MemberRepository memberRepo;
    private final MemberAssetRepository assetRepo;

    @Override
    @Transactional
    public void recordTrade(TradeRequestDto dto) {
        Member member = memberRepo.findById(dto.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // ✅ null-safe BigDecimal 변환
        BigDecimal goldPrice = dto.getGoldPrice() != null ? dto.getGoldPrice() : BigDecimal.ZERO;
        BigDecimal quantity = dto.getQuantity() != null ? dto.getQuantity() : BigDecimal.ZERO;
        BigDecimal total = goldPrice.multiply(quantity);

        // ✅ 거래 저장
        SimulationTrade trade = SimulationTrade.builder()
                .member(member)
                .tradeDate(LocalDate.now())
                .tradeType(dto.getTradeType() != null ? dto.getTradeType() : "매수")
                .goldPrice(goldPrice)
                .quantity(quantity)
                .amount(total)
                .build();

        tradeRepo.save(trade);

        // ✅ 결과 저장
        SimulationResult result = SimulationResult.builder()
                .trade(trade)
                .member(member)
                .resultDate(LocalDate.now())
                .pnl(BigDecimal.ZERO)
                .predict(dto.getPredict())
                .actual(null)
                .favorite(false)
                .note("거래 완료: " + trade.getTradeType())
                .build();

        resultRepo.save(result);

        // ✅ 자산 업데이트
        assetRepo.findByMember_MemberNo(dto.getMemberNo())
                .ifPresent(asset -> {
                    long current = asset.getBalance();
                    long tradeValue = total != null ? total.longValue() : 0;
                    long newBalance = trade.getTradeType().equals("매수")
                            ? current - tradeValue
                            : current + tradeValue;
                    asset.setBalance(newBalance);
                    assetRepo.save(asset);
                });

        System.out.println("✅ 거래 저장 완료: memberNo=" + dto.getMemberNo() +
                ", total=" + total + ", type=" + dto.getTradeType());
    }
}