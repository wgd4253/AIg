package com.exem.exemone2.dto.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbSqlSummary {
    private String sqlId;
    private String sqlText;
    private Long executions;
    private Long elapsedTime;
    private Long cpuTime;
    private Long bufferGets;
    private Long diskReads;
}
