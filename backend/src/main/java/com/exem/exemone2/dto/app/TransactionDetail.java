package com.exem.exemone2.dto.app;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    private String txnId;
    private String instanceId;
    private String serviceName;
    private String url;
    private Long responseTime;
    private String status;
    private String errorMessage;
    private Instant startTime;
    private Instant endTime;
    private List<CallTreeNode> callTree;
    private List<SqlExecution> sqlExecutions;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallTreeNode {
        private String className;
        private String methodName;
        private Long elapsed;
        private List<CallTreeNode> children;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SqlExecution {
        private String sqlHash;
        private String sqlText;
        private Long elapsed;
    }
}
