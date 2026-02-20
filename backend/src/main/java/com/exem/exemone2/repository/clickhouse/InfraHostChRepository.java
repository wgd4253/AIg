package com.exem.exemone2.repository.clickhouse;

import java.util.List;
import java.util.Map;

import com.exem.exemone2.dto.infra.ProcessInfo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.common.MetricPoint;
import com.exem.exemone2.dto.common.TimeRange;

@Repository
public class InfraHostChRepository {

    private final JdbcTemplate jdbc;

    public InfraHostChRepository(@Qualifier("clickhouseJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MetricPoint> findCpuMetrics(String hostId, TimeRange range) {
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

    public List<MetricPoint> findMemoryMetrics(String hostId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(`total`) AS mem_total,
                       avg(`total` - available) AS mem_used,
                       avg(available) AS mem_available,
                       avg(cached) AS mem_cached,
                       avg(buffers) AS mem_buffers
                FROM infra_host_stat
                WHERE host_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("total", rs.getDouble("mem_total"),
                               "used", rs.getDouble("mem_used"),
                               "available", rs.getDouble("mem_available"),
                               "cached", rs.getDouble("mem_cached"))),
                hostId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findDiskMetrics(String hostId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       avg(read_bytes) AS read_bytes,
                       avg(write_bytes) AS write_bytes,
                       avg(util) AS util
                FROM infra_disk_stat
                WHERE host_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("readBytes", rs.getDouble("read_bytes"),
                               "writeBytes", rs.getDouble("write_bytes"),
                               "diskUtil", rs.getDouble("util"))),
                hostId,
                range.fromForCh(),
                range.toForCh());
    }

    public List<MetricPoint> findNetworkMetrics(String hostId, TimeRange range) {
        return jdbc.query("""
                SELECT toStartOfInterval(collect_time, INTERVAL 1 MINUTE) AS ts,
                       sum(recv_bytes) AS recv_bytes,
                       sum(send_bytes) AS send_bytes,
                       sum(recv_errors) AS recv_errors,
                       sum(send_errors) AS send_errors
                FROM infra_net_stat
                WHERE host_id = ?
                  AND collect_time BETWEEN ? AND ?
                GROUP BY ts
                ORDER BY ts
                """,
                (rs, rowNum) -> new MetricPoint(
                        rs.getTimestamp("ts").toInstant(),
                        Map.of("recvBytes", rs.getDouble("recv_bytes"),
                               "sendBytes", rs.getDouble("send_bytes"),
                               "recvErrors", rs.getDouble("recv_errors"),
                               "sendErrors", rs.getDouble("send_errors"))),
                hostId,
                range.fromForCh(),
                range.toForCh());
    }

    public Double getLatestCpuUsage(String hostId) {
        List<Double> results = jdbc.query("""
                SELECT `user` + sys + io_wait + irq + soft_irq + steal AS cpu_usage
                FROM infra_host_stat
                WHERE host_id = ?
                ORDER BY collect_time DESC
                LIMIT 1
                """,
                (rs, rowNum) -> rs.getDouble("cpu_usage"),
                hostId);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<ProcessInfo> findProcesses(String hostId, int top) {
        return jdbc.query("""
                SELECT pid, user_name, cpu, rss, process_name
                FROM infra_process
                WHERE host_id = ?
                ORDER BY collect_time DESC, cpu DESC
                LIMIT ?
                """,
                (rs, rowNum) -> ProcessInfo.builder()
                        .pid(rs.getLong("pid"))
                        .name(rs.getString("process_name"))
                        .user(rs.getString("user_name"))
                        .cpuUsage(rs.getDouble("cpu"))
                        .memoryBytes(rs.getLong("rss"))
                        .build(),
                hostId, top);
    }

    public Double getLatestMemoryUsage(String hostId) {
        List<Double> results = jdbc.query("""
                SELECT (`total` - available) * 100.0 / `total` AS mem_usage
                FROM infra_host_stat
                WHERE host_id = ? AND `total` > 0
                ORDER BY collect_time DESC
                LIMIT 1
                """,
                (rs, rowNum) -> rs.getDouble("mem_usage"),
                hostId);
        return results.isEmpty() ? null : results.get(0);
    }
}
