package com.exem.exemone2.service.infra;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.infra.HostDetail;
import com.exem.exemone2.dto.infra.HostStatus;
import com.exem.exemone2.dto.infra.HostSummary;
import com.exem.exemone2.dto.infra.ProcessInfo;

@Service
public class InfraHostService {

    public List<HostSummary> getHosts() {
        // TODO: PostgreSQL에서 호스트 목록 조회
        return List.of();
    }

    public HostDetail getHost(String hostKey) {
        // TODO: PostgreSQL에서 호스트 상세 조회
        return null;
    }

    public HostStatus getHostStatus(String hostKey) {
        // TODO: Redis에서 실시간 상태 조회
        return null;
    }

    public List<MetricPoint> getCpuMetrics(String hostKey, TimeRange range) {
        // TODO: ClickHouse에서 CPU 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getMemoryMetrics(String hostKey, TimeRange range) {
        // TODO: ClickHouse에서 메모리 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getDiskMetrics(String hostKey, TimeRange range) {
        // TODO: ClickHouse에서 디스크 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getNetworkMetrics(String hostKey, TimeRange range) {
        // TODO: ClickHouse에서 네트워크 메트릭 조회
        return List.of();
    }

    public List<ProcessInfo> getProcesses(String hostKey, int top) {
        // TODO: Redis 또는 ClickHouse에서 프로세스 목록 조회
        return List.of();
    }
}
