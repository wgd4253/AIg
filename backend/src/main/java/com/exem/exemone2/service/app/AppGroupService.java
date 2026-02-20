package com.exem.exemone2.service.app;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.app.AppGroup;
import com.exem.exemone2.dto.app.AppInstance;
import com.exem.exemone2.dto.app.AppInstanceStatus;
import com.exem.exemone2.dto.app.SqlDetail;
import com.exem.exemone2.dto.app.SqlSummary;
import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.repository.clickhouse.AppChRepository;
import com.exem.exemone2.repository.postgres.AppPgRepository;
import com.exem.exemone2.repository.redis.AppRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppGroupService {

    private final AppPgRepository pgRepo;
    private final AppChRepository chRepo;
    private final AppRedisRepository redisRepo;

    public List<AppGroup> getGroups() {
        return pgRepo.findAllGroups();
    }

    public List<AppInstance> getInstances(String groupId) {
        return pgRepo.findInstancesByGroupId(groupId);
    }

    public AppInstance getInstance(String instanceId) {
        return pgRepo.findInstanceById(instanceId);
    }

    public AppInstanceStatus getInstanceStatus(String instanceId) {
        return redisRepo.findInstanceStatus(instanceId);
    }

    public List<MetricPoint> getTpsMetrics(String instanceId, TimeRange range) {
        return chRepo.findTpsMetrics(instanceId, range);
    }

    public List<MetricPoint> getResponseTimeMetrics(String instanceId, TimeRange range) {
        return chRepo.findResponseTimeMetrics(instanceId, range);
    }

    public List<MetricPoint> getErrorRateMetrics(String instanceId, TimeRange range) {
        return chRepo.findErrorRateMetrics(instanceId, range);
    }

    public List<MetricPoint> getActiveThreadMetrics(String instanceId, TimeRange range) {
        return chRepo.findActiveThreadMetrics(instanceId, range);
    }

    public List<MetricPoint> getJvmMetrics(String instanceId, TimeRange range) {
        return chRepo.findJvmMetrics(instanceId, range);
    }

    public List<SqlSummary> getTopSql(String instanceId, TimeRange range, int top) {
        return chRepo.findTopSql(instanceId, range, top);
    }

    public SqlDetail getSqlDetail(String sqlHash) {
        // SQL text lookup would require a separate ClickHouse query
        return null;
    }
}
