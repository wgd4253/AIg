package com.exem.exemone2.service.app;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.app.TransactionDetail;
import com.exem.exemone2.dto.app.TransactionSummary;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.repository.clickhouse.AppChRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppTransactionService {

    private final AppChRepository chRepo;

    public List<TransactionSummary> getTransactions(String instanceId, TimeRange range,
                                                     int page, int size) {
        int offset = page * size;
        return chRepo.findTransactions(instanceId, range, offset, size);
    }

    public TransactionDetail getTransaction(String txnId) {
        return chRepo.findTransactionById(txnId);
    }

    public List<TransactionSummary> getTopSlowTransactions(String instanceId, TimeRange range,
                                                            int top) {
        return chRepo.findTopSlowTransactions(instanceId, range, top);
    }

    public List<TransactionSummary> getTopErrorTransactions(String instanceId, TimeRange range,
                                                             int top) {
        return chRepo.findTopErrorTransactions(instanceId, range, top);
    }
}
