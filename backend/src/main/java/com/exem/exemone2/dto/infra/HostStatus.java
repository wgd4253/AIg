package com.exem.exemone2.dto.infra;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostStatus {
    private String hostKey;
    private String status;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private Instant lastUpdated;
}
