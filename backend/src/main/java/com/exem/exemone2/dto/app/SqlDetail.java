package com.exem.exemone2.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlDetail {
    private String sqlHash;
    private String sqlText;
    private Long executionCount;
    private Long totalElapsed;
    private Long avgElapsed;
    private Long maxElapsed;
    private Long minElapsed;
}
