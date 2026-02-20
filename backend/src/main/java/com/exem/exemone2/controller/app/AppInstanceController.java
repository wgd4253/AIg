package com.exem.exemone2.controller.app;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exem.exemone2.dto.app.AppInstance;
import com.exem.exemone2.dto.app.AppInstanceStatus;
import com.exem.exemone2.dto.app.SqlDetail;
import com.exem.exemone2.dto.app.SqlSummary;
import com.exem.exemone2.dto.common.ApiResponse;
import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.service.app.AppGroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppInstanceController {

    private final AppGroupService appGroupService;

    @GetMapping("/instances/{instanceId}")
    public ApiResponse<AppInstance> getInstance(@PathVariable String instanceId) {
        return ApiResponse.ok(appGroupService.getInstance(instanceId));
    }

    @GetMapping("/instances/{instanceId}/status")
    public ApiResponse<AppInstanceStatus> getInstanceStatus(@PathVariable String instanceId) {
        return ApiResponse.ok(appGroupService.getInstanceStatus(instanceId));
    }

    @GetMapping("/instances/{instanceId}/metrics/tps")
    public ApiResponse<List<MetricPoint>> getTpsMetrics(@PathVariable String instanceId,
                                                         TimeRange range) {
        return ApiResponse.ok(appGroupService.getTpsMetrics(instanceId, range));
    }

    @GetMapping("/instances/{instanceId}/metrics/response-time")
    public ApiResponse<List<MetricPoint>> getResponseTimeMetrics(@PathVariable String instanceId,
                                                                  TimeRange range) {
        return ApiResponse.ok(appGroupService.getResponseTimeMetrics(instanceId, range));
    }

    @GetMapping("/instances/{instanceId}/metrics/error-rate")
    public ApiResponse<List<MetricPoint>> getErrorRateMetrics(@PathVariable String instanceId,
                                                               TimeRange range) {
        return ApiResponse.ok(appGroupService.getErrorRateMetrics(instanceId, range));
    }

    @GetMapping("/instances/{instanceId}/metrics/active-threads")
    public ApiResponse<List<MetricPoint>> getActiveThreadMetrics(@PathVariable String instanceId,
                                                                  TimeRange range) {
        return ApiResponse.ok(appGroupService.getActiveThreadMetrics(instanceId, range));
    }

    @GetMapping("/instances/{instanceId}/metrics/jvm")
    public ApiResponse<List<MetricPoint>> getJvmMetrics(@PathVariable String instanceId,
                                                         TimeRange range) {
        return ApiResponse.ok(appGroupService.getJvmMetrics(instanceId, range));
    }

    @GetMapping("/instances/{instanceId}/sql/top")
    public ApiResponse<List<SqlSummary>> getTopSql(@PathVariable String instanceId,
                                                    TimeRange range,
                                                    @RequestParam(defaultValue = "10") int top) {
        return ApiResponse.ok(appGroupService.getTopSql(instanceId, range, top));
    }

    @GetMapping("/sql/{sqlHash}")
    public ApiResponse<SqlDetail> getSqlDetail(@PathVariable String sqlHash) {
        return ApiResponse.ok(appGroupService.getSqlDetail(sqlHash));
    }
}
