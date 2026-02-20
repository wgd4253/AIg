package com.exem.exemone2.service.db;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.db.DbSqlDetail;
import com.exem.exemone2.dto.db.DbSqlPlan;
import com.exem.exemone2.dto.db.DbSqlSummary;

@Service
public class DbSqlService {

    public List<DbSqlSummary> getTopSql(String instanceId, TimeRange range, int top) {
        // TODO: ClickHouse에서 Top N SQL 조회
        return List.of();
    }

    public DbSqlDetail getSqlDetail(String sqlId) {
        // TODO: PostgreSQL/ClickHouse에서 SQL 상세 조회
        return null;
    }

    public DbSqlPlan getSqlPlan(String sqlId) {
        // TODO: PostgreSQL에서 실행 계획 조회
        return null;
    }
}
