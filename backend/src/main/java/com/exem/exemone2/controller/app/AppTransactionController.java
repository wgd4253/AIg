package com.exem.exemone2.controller.app;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exem.exemone2.dto.app.TransactionDetail;
import com.exem.exemone2.dto.app.TransactionSummary;
import com.exem.exemone2.dto.common.ApiResponse;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.service.app.AppTransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppTransactionController {

    private final AppTransactionService appTransactionService;

    @GetMapping("/instances/{instanceId}/transactions")
    public ApiResponse<List<TransactionSummary>> getTransactions(
            @PathVariable String instanceId,
            TimeRange range,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(appTransactionService.getTransactions(instanceId, range, page, size));
    }

    @GetMapping("/transactions/{txnId}")
    public ApiResponse<TransactionDetail> getTransaction(@PathVariable String txnId) {
        return ApiResponse.ok(appTransactionService.getTransaction(txnId));
    }

    @GetMapping("/instances/{instanceId}/transactions/top-slow")
    public ApiResponse<List<TransactionSummary>> getTopSlowTransactions(
            @PathVariable String instanceId,
            TimeRange range,
            @RequestParam(defaultValue = "10") int top) {
        return ApiResponse.ok(appTransactionService.getTopSlowTransactions(instanceId, range, top));
    }

    @GetMapping("/instances/{instanceId}/transactions/top-error")
    public ApiResponse<List<TransactionSummary>> getTopErrorTransactions(
            @PathVariable String instanceId,
            TimeRange range,
            @RequestParam(defaultValue = "10") int top) {
        return ApiResponse.ok(appTransactionService.getTopErrorTransactions(instanceId, range, top));
    }
}
