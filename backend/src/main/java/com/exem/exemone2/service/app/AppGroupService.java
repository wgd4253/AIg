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

@Service
public class AppGroupService {

    public List<AppGroup> getGroups() {
        // TODO: PostgreSQL에서 WAS 그룹 목록 조회
        return List.of();
    }

    public List<AppInstance> getInstances(String groupId) {
        // TODO: PostgreSQL에서 그룹 내 인스턴스 목록 조회
        return List.of();
    }

    public AppInstance getInstance(String instanceId) {
        // TODO: PostgreSQL에서 인스턴스 상세 조회
        return null;
    }

    public AppInstanceStatus getInstanceStatus(String instanceId) {
        // TODO: Redis에서 실시간 상태 조회
        return null;
    }

    public List<MetricPoint> getTpsMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 TPS 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getResponseTimeMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 응답시간 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getErrorRateMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 에러율 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getActiveThreadMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 액티브 스레드 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getJvmMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 JVM 메트릭 조회
        return List.of();
    }

    public List<SqlSummary> getTopSql(String instanceId, TimeRange range, int top) {
        // TODO: ClickHouse에서 Top N SQL 조회
        return List.of();
    }

    public SqlDetail getSqlDetail(String sqlHash) {
        // TODO: PostgreSQL/ClickHouse에서 SQL 상세 조회
        return null;
    }
}
