package com.goldSys.BE.metrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SeriesPointDto {
    private LocalDate date;
    private Double value;
}
