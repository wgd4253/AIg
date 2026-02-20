package com.exem.exemone2.dto.infra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostDetail {
    private String hostKey;
    private String hostname;
    private String ip;
    private String os;
    private String kernelVersion;
    private Integer cpuCores;
    private Long totalMemoryBytes;
    private Long totalDiskBytes;
    private String status;
}
