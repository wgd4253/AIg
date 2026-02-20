package com.exem.exemone2.dto.common;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeRange {
    private Instant from;
    private Instant to;
    private String interval;
}
