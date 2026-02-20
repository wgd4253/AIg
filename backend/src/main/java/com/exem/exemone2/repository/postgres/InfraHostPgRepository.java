package com.exem.exemone2.repository.postgres;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.infra.HostDetail;
import com.exem.exemone2.dto.infra.HostSummary;

@Repository
public class InfraHostPgRepository {

    private final JdbcTemplate jdbc;

    public InfraHostPgRepository(@Qualifier("pgJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<HostSummary> findAllHosts() {
        return jdbc.query("""
                SELECT h.host_id, h.hostname, h.ip_addresses, h.os_type,
                       h.host_key
                FROM xm_infra_host h
                WHERE h.deleted = false
                ORDER BY h.hostname
                """,
                (rs, rowNum) -> HostSummary.builder()
                        .hostKey(rs.getString("host_id"))
                        .hostname(rs.getString("hostname"))
                        .ip(rs.getString("ip_addresses"))
                        .os(rs.getString("os_type"))
                        .build());
    }

    public HostDetail findByHostKey(String hostKey) {
        List<HostDetail> results = jdbc.query("""
                SELECT h.host_id, h.hostname, h.ip_addresses, h.os_type, h.os_version,
                       h.logical_core, h.host_key
                FROM xm_infra_host h
                WHERE h.host_id = ? AND h.deleted = false
                """,
                (rs, rowNum) -> HostDetail.builder()
                        .hostKey(rs.getString("host_id"))
                        .hostname(rs.getString("hostname"))
                        .ip(rs.getString("ip_addresses"))
                        .os(rs.getString("os_type"))
                        .kernelVersion(rs.getString("os_version"))
                        .cpuCores(rs.getObject("logical_core") != null ? rs.getInt("logical_core") : null)
                        .build(),
                hostKey);
        return results.isEmpty() ? null : results.get(0);
    }
}
