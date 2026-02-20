package com.exem.exemone2.dto.infra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInfo {
    private Long pid;
    private String name;
    private String user;
    private Double cpuUsage;
    private Long memoryBytes;
    private String status;
}
