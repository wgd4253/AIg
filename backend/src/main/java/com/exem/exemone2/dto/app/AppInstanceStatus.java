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
public class AppInstanceStatus {
    private String instanceId;
    private String status;
    private Double tps;
    private Double avgResponseTime;
    private Integer activeThreads;
    private Double errorRate;
    private Instant lastUpdated;
}
