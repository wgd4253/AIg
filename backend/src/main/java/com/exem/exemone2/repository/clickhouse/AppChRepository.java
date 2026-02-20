package com.exem.exemone2.repository.clickhouse;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.app.SqlSummary;
import com.exem.exemone2.dto.app.TransactionDetail;
import com.exem.exemone2.dto.app.TransactionSummary;
import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;

@Repository
public class AppChRepository {

    private final JdbcTemplate jdbc;

    public AppChRepository(@Qualifier("clickhouseJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MetricPoint> findTpsMetrics(String wasId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       sum(txn_end_count) / 60.0 AS tps
                FROM application_was_stat_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("tps", rs.getDouble("tps"))),
                wasId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findResponseTimeMetrics(String wasId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(CASE WHEN txn_end_count > 0 THEN txn_elapse / txn_end_count ELSE 0 END) AS avg_response_time,
                       max(txn_elapse_max) AS max_response_time
                FROM application_was_stat_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("avgResponseTime", rs.getDouble("avg_response_time"),
                               "maxResponseTime", rs.getDouble("max_response_time"))),
                wasId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findErrorRateMetrics(String wasId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       sum(txn_error_count) AS error_count,
                       sum(txn_end_count) AS total_count,
                       CASE WHEN sum(txn_end_count) > 0
                            THEN sum(txn_error_count) * 100.0 / sum(txn_end_count)
                            ELSE 0 END AS error_rate
                FROM application_was_stat_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("errorRate", rs.getDouble("error_rate"),
                               "errorCount", rs.getDouble("error_count"))),
                wasId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findActiveThreadMetrics(String wasId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(active_txn_count) AS active_threads
                FROM application_was_stat_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("activeThreads", rs.getDouble("active_threads"))),
                wasId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findJvmMetrics(String wasId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(s.collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(s.jvm_heap_size) AS heap_size,
                       avg(s.jvm_free_heap) AS free_heap,
                       avg(s.jvm_max_heap) AS max_heap,
                       avg(s.jvm_cpu_usage) AS jvm_cpu,
                       max(j.ygc) AS ygc,
                       max(j.fgc) AS fgc
                FROM application_was_stat_v2 s
                LEFT JOIN application_jvm_stat_v2 j
                    ON s.was_id = j.was_id
                    AND toStartOfInterval(s.collect_time, INTERVAL 1 MINUTE)
                      = toStartOfInterval(j.collect_time, INTERVAL 1 MINUTE)
                WHERE s.was_id = ?
                  AND s.collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("heapSize", rs.getDouble("heap_size"),
                               "freeHeap", rs.getDouble("free_heap"),
                               "maxHeap", rs.getDouble("max_heap"),
                               "jvmCpu", rs.getDouble("jvm_cpu"),
                               "ygc", rs.getDouble("ygc"),
                               "fgc", rs.getDouble("fgc"))),
                wasId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<TransactionSummary> findTransactions(String wasId, TimeRange range,
                                                      int offset, int limit) {
        return jdbc.query("""
                SELECT txn_id, txn_name, elapsed_time, start_time,
                       CASE WHEN exception = true THEN 'ERROR' ELSE 'OK' END AS status
                FROM application_active_txn_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                ORDER BY start_time DESC
                LIMIT ? OFFSET ?
                """,
                (rs, rowNum) -> TransactionSummary.builder()
                        .txnId(rs.getString("txn_id"))
                        .serviceName(rs.getString("txn_name"))
                        .responseTime(rs.getLong("elapsed_time"))
                        .startTime(rs.getTimestamp("start_time").toInstant())
                        .status(rs.getString("status"))
                        .build(),
                wasId,
                range.fromForCh(),
                range.toForCh(),
                limit, offset);
    }

    public TransactionDetail findTransactionById(String txnId) {
        List<TransactionDetail> results = jdbc.query("""
                SELECT was_id, txn_id, txn_name, elapsed_time, start_time,
                       client_ip, class_name, method_name, sql_id,
                       CASE WHEN exception = true THEN 'ERROR' ELSE 'OK' END AS status
                FROM application_active_txn_v2
                WHERE txn_id = ?
                ORDER BY collect_time DESC
                LIMIT 1
                """,
                (rs, rowNum) -> TransactionDetail.builder()
                        .txnId(rs.getString("txn_id"))
                        .instanceId(rs.getString("was_id"))
                        .serviceName(rs.getString("txn_name"))
                        .responseTime(rs.getLong("elapsed_time"))
                        .startTime(rs.getTimestamp("start_time").toInstant())
                        .status(rs.getString("status"))
                        .build(),
                txnId);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<TransactionSummary> findTopSlowTransactions(String wasId, TimeRange range, int top) {
        return jdbc.query("""
                SELECT txn_id, txn_name, elapsed_time, start_time,
                       CASE WHEN exception = true THEN 'ERROR' ELSE 'OK' END AS status
                FROM application_active_txn_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                ORDER BY elapsed_time DESC
                LIMIT ?
                """,
                (rs, rowNum) -> TransactionSummary.builder()
                        .txnId(rs.getString("txn_id"))
                        .serviceName(rs.getString("txn_name"))
                        .responseTime(rs.getLong("elapsed_time"))
                        .startTime(rs.getTimestamp("start_time").toInstant())
                        .status(rs.getString("status"))
                        .build(),
                wasId,
                range.fromForCh(),
                range.toForCh(),
                top);
    }

    public List<TransactionSummary> findTopErrorTransactions(String wasId, TimeRange range, int top) {
        return jdbc.query("""
                SELECT txn_id, txn_name, elapsed_time, start_time, 'ERROR' AS status
                FROM application_active_txn_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                  AND exception = true
                ORDER BY elapsed_time DESC
                LIMIT ?
                """,
                (rs, rowNum) -> TransactionSummary.builder()
                        .txnId(rs.getString("txn_id"))
                        .serviceName(rs.getString("txn_name"))
                        .responseTime(rs.getLong("elapsed_time"))
                        .startTime(rs.getTimestamp("start_time").toInstant())
                        .status(rs.getString("status"))
                        .build(),
                wasId,
                range.fromForCh(),
                range.toForCh(),
                top);
    }

    public List<SqlSummary> findTopSql(String wasId, TimeRange range, int top) {
        return jdbc.query("""
                SELECT sql_id,
                       count(*) AS exec_count,
                       sum(sql_elapse) AS total_elapsed,
                       avg(sql_elapse) AS avg_elapsed,
                       max(sql_elapse_max) AS max_elapsed
                FROM application_was_stat_v2
                WHERE was_id = ?
                  AND collect_time BETWEEN ? AND ?
                  AND sql_exec_count > 0
                GROUP BY sql_id
                ORDER BY total_elapsed DESC
                LIMIT ?
                """,
                (rs, rowNum) -> SqlSummary.builder()
                        .sqlHash(rs.getString("sql_id"))
                        .executionCount(rs.getLong("exec_count"))
                        .totalElapsed(rs.getLong("total_elapsed"))
                        .avgElapsed(rs.getLong("avg_elapsed"))
                        .maxElapsed(rs.getLong("max_elapsed"))
                        .build(),
                wasId,
                range.fromForCh(),
                range.toForCh(),
                top);
    }
}
