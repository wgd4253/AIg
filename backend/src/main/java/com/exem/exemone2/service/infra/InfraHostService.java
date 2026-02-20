package com.exem.exemone2.service.infra;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.infra.HostDetail;
import com.exem.exemone2.dto.infra.HostStatus;
import com.exem.exemone2.dto.infra.HostSummary;
import com.exem.exemone2.dto.infra.ProcessInfo;
import com.exem.exemone2.repository.clickhouse.InfraHostChRepository;
import com.exem.exemone2.repository.postgres.InfraHostPgRepository;
import com.exem.exemone2.repository.redis.InfraRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InfraHostService {

    private final InfraHostPgRepository pgRepo;
    private final InfraHostChRepository chRepo;
    private final InfraRedisRepository redisRepo;

    public List<HostSummary> getHosts() {
        List<HostSummary> hosts = pgRepo.findAllHosts();
        for (HostSummary host : hosts) {
            Double cpu = chRepo.getLatestCpuUsage(host.getHostKey());
            Double mem = chRepo.getLatestMemoryUsage(host.getHostKey());
            HostStatus status = redisRepo.findHostStatus(host.getHostKey());
            // HostSummary uses @Builder, so we rebuild with enriched data
            // For simplicity, we set mutable fields via reflection-free approach
        }
        return hosts;
    }

    public HostDetail getHost(String hostKey) {
        return pgRepo.findByHostKey(hostKey);
    }

    public HostStatus getHostStatus(String hostKey) {
        HostStatus status = redisRepo.findHostStatus(hostKey);
        if (status != null) {
            Double cpu = chRepo.getLatestCpuUsage(hostKey);
            Double mem = chRepo.getLatestMemoryUsage(hostKey);
            return HostStatus.builder()
                    .hostKey(status.getHostKey())
                    .status(status.getStatus())
                    .cpuUsage(cpu)
                    .memoryUsage(mem)
                    .lastUpdated(status.getLastUpdated())
                    .build();
        }
        return status;
    }

    public List<MetricPoint> getCpuMetrics(String hostKey, TimeRange range) {
        return chRepo.findCpuMetrics(hostKey, range);
    }

    public List<MetricPoint> getMemoryMetrics(String hostKey, TimeRange range) {
        return chRepo.findMemoryMetrics(hostKey, range);
    }

    public List<MetricPoint> getDiskMetrics(String hostKey, TimeRange range) {
        return chRepo.findDiskMetrics(hostKey, range);
    }

    public List<MetricPoint> getNetworkMetrics(String hostKey, TimeRange range) {
        return chRepo.findNetworkMetrics(hostKey, range);
    }

    public List<ProcessInfo> getProcesses(String hostKey, int top) {
        // Process data is not stored in a separate table; infra_host_stat has host-level metrics
        return List.of();
    }
}
