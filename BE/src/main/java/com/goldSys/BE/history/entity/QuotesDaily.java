package com.goldSys.BE.history.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "QUOTES_DAILY")
public class QuotesDaily {
    @Id
    @Column(name = "DATE", nullable = false)
    private LocalDate date;

    @Column(name = "KRW_G_OPEN")
    private Double krwGOpen;

    @Column(name = "KRW_G_CLOSE")
    private Double krwGClose;

    @Column(name = "USD_OZ_OPEN")
    private Double usdOzOpen;

    @Column(name = "USD_OZ_CLOSE")
    private Double usdOzClose;

    @Column(name = "VIX")
    private Double vix;

    @Column(name = "ETF_VOLUME")
    private Double etfVolume;

    @Column(name = "FX_RATE")
    private Double fxRate;
}