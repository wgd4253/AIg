package com.exem.exemone2.dto.app;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummary {
    private String txnId;
    private String serviceName;
    private String url;
    private Long responseTime;
    private String status;
    private Instant startTime;
    private Instant endTime;
}
