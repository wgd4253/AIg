package com.exem.exemone2.repository.clickhouse;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.db.DbSession;
import com.exem.exemone2.dto.db.DbSqlDetail;
import com.exem.exemone2.dto.db.DbSqlSummary;
import com.exem.exemone2.dto.db.LockTreeNode;

@Repository
public class DbChRepository {

    private final JdbcTemplate jdbc;

    public DbChRepository(@Qualifier("clickhouseJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MetricPoint> findActiveSessionMetrics(String instanceId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(active_backend) AS active_sessions,
                       avg(numbackends) AS total_backends
                FROM postgresql_dbstat
                WHERE instance_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("activeSessions", rs.getDouble("active_sessions"),
                               "totalBackends", rs.getDouble("total_backends"))),
                instanceId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findWaitEventMetrics(String instanceId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       wait_event_type,
                       count(*) AS cnt
                FROM postgresql_active_backend
                WHERE instance_id = ?
                  AND collect_time BETWEEN ? AND ?
                  AND wait_event_type IS NOT NULL
                  AND wait_event_type != ''
                GROUP BY ts, wait_event_type
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of(rs.getString("wait_event_type"), rs.getDouble("cnt"))),
                instanceId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findCpuMetricsByHostId(String hostId, TimeRange range) {
        if (hostId == null || hostId.isBlank()) {
            return List.of();
        }
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(`user` + sys + io_wait + irq + soft_irq + steal) AS cpu_usage,
                       avg(`user`) AS cpu_user,
                       avg(sys) AS cpu_sys,
                       avg(io_wait) AS cpu_iowait
                FROM infra_host_stat
                WHERE host_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("cpuUsage", rs.getDouble("cpu_usage"),
                               "cpuUser", rs.getDouble("cpu_user"),
                               "cpuSys", rs.getDouble("cpu_sys"),
                               "cpuIowait", rs.getDouble("cpu_iowait"))),
                hostId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findMemoryMetrics(String instanceId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(numbackends) AS connections,
                       avg(temp_bytes) AS temp_bytes
                FROM postgresql_dbstat
                WHERE instance_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("connections", rs.getDouble("connections"),
                               "tempBytes", rs.getDouble("temp_bytes"))),
                instanceId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findTablespaceMetrics(String instanceId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       sum(tup_inserted + tup_updated + tup_deleted) AS write_ops,
                       sum(tup_returned + tup_fetched) AS read_ops
                FROM postgresql_dbstat
                WHERE instance_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("writeOps", rs.getDouble("write_ops"),
                               "readOps", rs.getDouble("read_ops"))),
                instanceId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findIoMetrics(String instanceId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       sum(tup_returned) AS tup_returned,
                       sum(tup_fetched) AS tup_fetched,
                       sum(xact_commit) AS commits,
                       sum(xact_rollback) AS rollbacks
                FROM postgresql_dbstat
                WHERE instance_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("tupReturned", rs.getDouble("tup_returned"),
                               "tupFetched", rs.getDouble("tup_fetched"),
                               "commits", rs.getDouble("commits"),
                               "rollbacks", rs.getDouble("rollbacks"))),
                instanceId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<DbSession> findActiveSessions(String instanceId) {
        return jdbc.query("""
                SELECT pid, usename, datname, application_name,
                       state, wait_event_type, wait_event,
                       elapsed_time, query_start
                FROM postgresql_active_backend
                WHERE instance_id = ?
                ORDER BY collect_time DESC
                LIMIT 100
                """,
                (rs, rowNum) -> DbSession.builder()
                        .sessionId(String.valueOf(rs.getInt("pid")))
                        .username(rs.getString("usename"))
                        .program(rs.getString("application_name"))
                        .status(rs.getString("state"))
                        .waitEvent(rs.getString("wait_event"))
                        .elapsedTime(rs.getObject("elapsed_time") != null
                                ? rs.getLong("elapsed_time") : null)
                        .build(),
                instanceId);
    }

    public DbSession findSession(String instanceId, String sessionId) {
        List<DbSession> results = jdbc.query("""
                SELECT pid, usename, datname, application_name,
                       state, wait_event_type, wait_event,
                       elapsed_time, query_start
                FROM postgresql_active_backend
                WHERE instance_id = ?
                  AND toString(pid) = ?
                ORDER BY collect_time DESC
                LIMIT 1
                """,
                (rs, rowNum) -> DbSession.builder()
                        .sessionId(String.valueOf(rs.getInt("pid")))
                        .username(rs.getString("usename"))
                        .program(rs.getString("application_name"))
                        .status(rs.getString("state"))
                        .waitEvent(rs.getString("wait_event"))
                        .elapsedTime(rs.getObject("elapsed_time") != null
                                ? rs.getLong("elapsed_time") : null)
                        .build(),
                instanceId, sessionId);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<LockTreeNode> findLockTree(String instanceId) {
        return jdbc.query("""
                SELECT waiter_pid, waiter_user, waiter_query,
                       holder_pid, holder_user, holder_query,
                       lock_type, lock_mode
                FROM postgresql_locktree
                WHERE instance_id = ?
                ORDER BY collect_time DESC
                LIMIT 200
                """,
                (rs, rowNum) -> LockTreeNode.builder()
                        .sessionId(String.valueOf(rs.getInt("waiter_pid")))
                        .username(rs.getString("waiter_user"))
                        .sqlText(rs.getString("waiter_query"))
                        .lockType(rs.getString("lock_type"))
                        .lockMode(rs.getString("lock_mode"))
                        .waiters(List.of())
                        .build(),
                instanceId);
    }

    public DbSqlDetail findSqlDetail(String sqlId) {
        List<DbSqlDetail> results = jdbc.query("""
                SELECT t.sql_id, t.sql_fulltext,
                       e.executions, e.elapsed_time, e.cpu_time,
                       e.buffer_gets, e.disk_reads, e.rows_processed
                FROM postgresql_sql_text t
                LEFT JOIN (
                    SELECT sql_id,
                           sum(executions) AS executions,
                           sum(elapsed_time) AS elapsed_time,
                           sum(cpu_time) AS cpu_time,
                           sum(buffer_gets) AS buffer_gets,
                           sum(disk_reads) AS disk_reads,
                           sum(rows_processed) AS rows_processed
                    FROM postgresql_sql_elapse
                    WHERE sql_id = ?
                    GROUP BY sql_id
                ) e ON t.sql_id = e.sql_id
                WHERE t.sql_id = ?
                LIMIT 1
                """,
                (rs, rowNum) -> DbSqlDetail.builder()
                        .sqlId(rs.getString("sql_id"))
                        .sqlText(rs.getString("sql_fulltext"))
                        .executions(rs.getObject("executions") != null ? rs.getLong("executions") : 0L)
                        .elapsedTime(rs.getObject("elapsed_time") != null ? rs.getLong("elapsed_time") : 0L)
                        .cpuTime(rs.getObject("cpu_time") != null ? rs.getLong("cpu_time") : 0L)
                        .bufferGets(rs.getObject("buffer_gets") != null ? rs.getLong("buffer_gets") : 0L)
                        .diskReads(rs.getObject("disk_reads") != null ? rs.getLong("disk_reads") : 0L)
                        .rowsProcessed(rs.getObject("rows_processed") != null ? rs.getLong("rows_processed") : 0L)
                        .build(),
                sqlId, sqlId);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<DbSqlSummary> findTopSql(String instanceId, TimeRange range, int top) {
        return jdbc.query("""
                SELECT sql_id,
                       sum(executions) AS executions,
                       sum(elapsed_time) AS elapsed_time,
                       sum(cpu_time) AS cpu_time,
                       sum(buffer_gets) AS buffer_gets,
                       sum(disk_reads) AS disk_reads
                FROM postgresql_sql_stat_v2
                WHERE instance_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY sql_id
                ORDER BY elapsed_time DESC
                LIMIT ?
                """,
                (rs, rowNum) -> DbSqlSummary.builder()
                        .sqlId(rs.getString("sql_id"))
                        .executions(rs.getLong("executions"))
                        .elapsedTime(rs.getLong("elapsed_time"))
                        .cpuTime(rs.getLong("cpu_time"))
                        .bufferGets(rs.getLong("buffer_gets"))
                        .diskReads(rs.getLong("disk_reads"))
                        .build(),
                instanceId,
                range.fromForCh(),
                range.toForCh(),
                top);
    }
}
