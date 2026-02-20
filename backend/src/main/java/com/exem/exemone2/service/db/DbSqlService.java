package com.exem.exemone2.service.db;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.db.DbSqlDetail;
import com.exem.exemone2.dto.db.DbSqlPlan;
import com.exem.exemone2.dto.db.DbSqlSummary;
import com.exem.exemone2.repository.clickhouse.DbChRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DbSqlService {

    private final DbChRepository chRepo;

    public List<DbSqlSummary> getTopSql(String instanceId, TimeRange range, int top) {
        return chRepo.findTopSql(instanceId, range, top);
    }

    public DbSqlDetail getSqlDetail(String sqlId) {
        return chRepo.findSqlDetail(sqlId);
    }

    public DbSqlPlan getSqlPlan(String sqlId) {
        // No postgresql_sql_plan table exists in ClickHouse (only oracle_sql_plan)
        // Return null until PostgreSQL plan collection is implemented
        return null;
    }
}
