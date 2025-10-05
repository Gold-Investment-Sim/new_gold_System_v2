package com.goldSys.BE.metrics.service.impl;

import com.goldSys.BE.history.entity.QuotesDaily;
import com.goldSys.BE.metrics.dto.SeriesPointDto;
import com.goldSys.BE.metrics.repository.QuotesDailyRepository;
import com.goldSys.BE.metrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final QuotesDailyRepository repo;

    @Override
    public List<SeriesPointDto> getSeries(String metric, LocalDate from, LocalDate to) {
        System.out.println("🔍 [DB조회] metric=" + metric + " 기간=" + from + " ~ " + to);
        List<QuotesDaily> rows = repo.findByDateBetweenOrderByDate(from, to);

        String getterName = switch (metric.toLowerCase()) {
            case "krw_g_open"   -> "getKrwGOpen";
            case "krw_g_close"  -> "getKrwGClose";
            case "usd_oz_open"  -> "getUsdOzOpen";
            case "usd_oz_close" -> "getUsdOzClose";
            case "vix"          -> "getVix";
            case "etf_volume"   -> "getEtfVolume";
            case "fx_rate"      -> "getFxRate";
            default -> throw new IllegalArgumentException("Unknown metric: " + metric);
        };

        return rows.stream()
                .map(r -> {
                    try {
                        Method getter = QuotesDaily.class.getMethod(getterName);
                        Double value = (Double) getter.invoke(r);
                        return new SeriesPointDto(r.getDate(), value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
