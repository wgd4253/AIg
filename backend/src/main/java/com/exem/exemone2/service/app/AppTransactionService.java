package com.exem.exemone2.service.app;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.app.TransactionDetail;
import com.exem.exemone2.dto.app.TransactionSummary;
import com.exem.exemone2.dto.common.TimeRange;

@Service
public class AppTransactionService {

    public List<TransactionSummary> getTransactions(String instanceId, TimeRange range,
                                                     int page, int size) {
        // TODO: ClickHouse에서 트랜잭션 목록 조회
        return List.of();
    }

    public TransactionDetail getTransaction(String txnId) {
        // TODO: ClickHouse에서 트랜잭션 상세 조회
        return null;
    }

    public List<TransactionSummary> getTopSlowTransactions(String instanceId, TimeRange range,
                                                            int top) {
        // TODO: ClickHouse에서 느린 트랜잭션 Top N 조회
        return List.of();
    }

    public List<TransactionSummary> getTopErrorTransactions(String instanceId, TimeRange range,
                                                             int top) {
        // TODO: ClickHouse에서 에러 트랜잭션 Top N 조회
        return List.of();
    }
}
