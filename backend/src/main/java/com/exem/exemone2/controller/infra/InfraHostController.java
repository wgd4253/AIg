package com.exem.exemone2.controller.infra;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exem.exemone2.dto.common.ApiResponse;
import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.infra.HostDetail;
import com.exem.exemone2.dto.infra.HostStatus;
import com.exem.exemone2.dto.infra.HostSummary;
import com.exem.exemone2.dto.infra.ProcessInfo;
import com.exem.exemone2.service.infra.InfraHostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/infra/hosts")
@RequiredArgsConstructor
public class InfraHostController {

    private final InfraHostService infraHostService;

    @GetMapping
    public ApiResponse<List<HostSummary>> getHosts() {
        return ApiResponse.ok(infraHostService.getHosts());
    }

    @GetMapping("/{hostKey}")
    public ApiResponse<HostDetail> getHost(@PathVariable String hostKey) {
        return ApiResponse.ok(infraHostService.getHost(hostKey));
    }

    @GetMapping("/{hostKey}/status")
    public ApiResponse<HostStatus> getHostStatus(@PathVariable String hostKey) {
        return ApiResponse.ok(infraHostService.getHostStatus(hostKey));
    }

    @GetMapping("/{hostKey}/metrics/cpu")
    public ApiResponse<List<MetricPoint>> getCpuMetrics(@PathVariable String hostKey,
                                                         TimeRange range) {
        return ApiResponse.ok(infraHostService.getCpuMetrics(hostKey, range));
    }

    @GetMapping("/{hostKey}/metrics/memory")
    public ApiResponse<List<MetricPoint>> getMemoryMetrics(@PathVariable String hostKey,
                                                            TimeRange range) {
        return ApiResponse.ok(infraHostService.getMemoryMetrics(hostKey, range));
    }

    @GetMapping("/{hostKey}/metrics/disk")
    public ApiResponse<List<MetricPoint>> getDiskMetrics(@PathVariable String hostKey,
                                                          TimeRange range) {
        return ApiResponse.ok(infraHostService.getDiskMetrics(hostKey, range));
    }

    @GetMapping("/{hostKey}/metrics/network")
    public ApiResponse<List<MetricPoint>> getNetworkMetrics(@PathVariable String hostKey,
                                                             TimeRange range) {
        return ApiResponse.ok(infraHostService.getNetworkMetrics(hostKey, range));
    }

    @GetMapping("/{hostKey}/processes")
    public ApiResponse<List<ProcessInfo>> getProcesses(@PathVariable String hostKey,
                                                        @RequestParam(defaultValue = "10") int top) {
        return ApiResponse.ok(infraHostService.getProcesses(hostKey, top));
    }
}
