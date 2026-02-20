package com.exem.exemone2.service.db;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.db.DbInstance;
import com.exem.exemone2.dto.db.DbInstanceStatus;

@Service
public class DbInstanceService {

    public List<DbInstance> getInstances() {
        // TODO: PostgreSQL에서 DB 인스턴스 목록 조회
        return List.of();
    }

    public DbInstance getInstance(String instanceId) {
        // TODO: PostgreSQL에서 인스턴스 상세 조회
        return null;
    }

    public DbInstanceStatus getInstanceStatus(String instanceId) {
        // TODO: Redis에서 실시간 상태 조회
        return null;
    }

    public List<MetricPoint> getActiveSessionMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 액티브 세션 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getWaitEventMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 대기 이벤트 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getCpuMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 CPU 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getMemoryMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 메모리 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getTablespaceMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 테이블스페이스 메트릭 조회
        return List.of();
    }

    public List<MetricPoint> getIoMetrics(String instanceId, TimeRange range) {
        // TODO: ClickHouse에서 I/O 메트릭 조회
        return List.of();
    }
}
