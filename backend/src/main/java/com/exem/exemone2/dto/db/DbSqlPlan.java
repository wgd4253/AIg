package com.exem.exemone2.dto.db;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbSqlPlan {
    private String sqlId;
    private String planHashValue;
    private List<PlanStep> steps;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanStep {
        private Integer id;
        private Integer parentId;
        private String operation;
        private String options;
        private String objectName;
        private Long cost;
        private Long cardinality;
        private Long bytes;
    }
}
