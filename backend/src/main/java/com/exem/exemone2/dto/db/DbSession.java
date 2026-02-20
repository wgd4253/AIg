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
public class DbSession {
    private String sessionId;
    private String username;
    private String program;
    private String status;
    private String waitEvent;
    private String sqlId;
    private String sqlText;
    private Long elapsedTime;
    private Instant startTime;
}
