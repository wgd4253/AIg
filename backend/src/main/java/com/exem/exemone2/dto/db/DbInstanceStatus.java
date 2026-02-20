package com.exem.exemone2.dto.db;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbInstanceStatus {
    private String instanceId;
    private String status;
    private Integer activeSessions;
    private Double cpuUsage;
    private Instant lastUpdated;
}
