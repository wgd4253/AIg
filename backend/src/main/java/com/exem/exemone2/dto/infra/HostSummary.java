package com.exem.exemone2.dto.infra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostSummary {
    private String hostKey;
    private String hostname;
    private String ip;
    private String os;
    private String status;
    private Double cpuUsage;
    private Double memoryUsage;
}
