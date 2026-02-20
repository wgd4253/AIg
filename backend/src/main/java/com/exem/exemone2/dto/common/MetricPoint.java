package com.exem.exemone2.dto.common;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetricPoint {
    private Instant timestamp;
    private Map<String, Double> values;
}
