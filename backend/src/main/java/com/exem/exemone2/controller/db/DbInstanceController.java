package com.exem.exemone2.controller.db;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exem.exemone2.dto.common.ApiResponse;
import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.db.DbInstance;
import com.exem.exemone2.dto.db.DbInstanceStatus;
import com.exem.exemone2.service.db.DbInstanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/db/instances")
@RequiredArgsConstructor
public class DbInstanceController {

    private final DbInstanceService dbInstanceService;

    @GetMapping
    public ApiResponse<List<DbInstance>> getInstances() {
        return ApiResponse.ok(dbInstanceService.getInstances());
    }

    @GetMapping("/{instanceId}")
    public ApiResponse<DbInstance> getInstance(@PathVariable String instanceId) {
        return ApiResponse.ok(dbInstanceService.getInstance(instanceId));
    }

    @GetMapping("/{instanceId}/status")
    public ApiResponse<DbInstanceStatus> getInstanceStatus(@PathVariable String instanceId) {
        return ApiResponse.ok(dbInstanceService.getInstanceStatus(instanceId));
    }

    @GetMapping("/{instanceId}/metrics/active-sessions")
    public ApiResponse<List<MetricPoint>> getActiveSessionMetrics(@PathVariable String instanceId,
                                                                    TimeRange range) {
        return ApiResponse.ok(dbInstanceService.getActiveSessionMetrics(instanceId, range));
    }

    @GetMapping("/{instanceId}/metrics/wait-events")
    public ApiResponse<List<MetricPoint>> getWaitEventMetrics(@PathVariable String instanceId,
                                                               TimeRange range) {
        return ApiResponse.ok(dbInstanceService.getWaitEventMetrics(instanceId, range));
    }

    @GetMapping("/{instanceId}/metrics/cpu")
    public ApiResponse<List<MetricPoint>> getCpuMetrics(@PathVariable String instanceId,
                                                         TimeRange range) {
        return ApiResponse.ok(dbInstanceService.getCpuMetrics(instanceId, range));
    }

    @GetMapping("/{instanceId}/metrics/memory")
    public ApiResponse<List<MetricPoint>> getMemoryMetrics(@PathVariable String instanceId,
                                                            TimeRange range) {
        return ApiResponse.ok(dbInstanceService.getMemoryMetrics(instanceId, range));
    }

    @GetMapping("/{instanceId}/metrics/tablespace")
    public ApiResponse<List<MetricPoint>> getTablespaceMetrics(@PathVariable String instanceId,
                                                                TimeRange range) {
        return ApiResponse.ok(dbInstanceService.getTablespaceMetrics(instanceId, range));
    }

    @GetMapping("/{instanceId}/metrics/io")
    public ApiResponse<List<MetricPoint>> getIoMetrics(@PathVariable String instanceId,
                                                        TimeRange range) {
        return ApiResponse.ok(dbInstanceService.getIoMetrics(instanceId, range));
    }
}
