package com.exem.exemone2.service.db;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.db.DbInstance;
import com.exem.exemone2.dto.db.DbInstanceStatus;
import com.exem.exemone2.repository.clickhouse.DbChRepository;
import com.exem.exemone2.repository.postgres.DbPgRepository;
import com.exem.exemone2.repository.redis.DbRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DbInstanceService {

    private final DbPgRepository pgRepo;
    private final DbChRepository chRepo;
    private final DbRedisRepository redisRepo;

    public List<DbInstance> getInstances() {
        return pgRepo.findAllInstances();
    }

    public DbInstance getInstance(String instanceId) {
        return pgRepo.findInstanceById(instanceId);
    }

    public DbInstanceStatus getInstanceStatus(String instanceId) {
        return redisRepo.findInstanceStatus(instanceId);
    }

    public List<MetricPoint> getActiveSessionMetrics(String instanceId, TimeRange range) {
        return chRepo.findActiveSessionMetrics(instanceId, range);
    }

    public List<MetricPoint> getWaitEventMetrics(String instanceId, TimeRange range) {
        return chRepo.findWaitEventMetrics(instanceId, range);
    }

    public List<MetricPoint> getCpuMetrics(String instanceId, TimeRange range) {
        String hostId = pgRepo.findHostIdByInstanceId(instanceId);
        return chRepo.findCpuMetricsByHostId(hostId, range);
    }

    public List<MetricPoint> getMemoryMetrics(String instanceId, TimeRange range) {
        return chRepo.findMemoryMetrics(instanceId, range);
    }

    public List<MetricPoint> getTablespaceMetrics(String instanceId, TimeRange range) {
        return chRepo.findTablespaceMetrics(instanceId, range);
    }

    public List<MetricPoint> getIoMetrics(String instanceId, TimeRange range) {
        return chRepo.findIoMetrics(instanceId, range);
    }
}
