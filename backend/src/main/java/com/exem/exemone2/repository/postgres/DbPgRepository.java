package com.exem.exemone2.repository.postgres;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.db.DbInstance;

@Repository
public class DbPgRepository {

    private final JdbcTemplate jdbc;

    public DbPgRepository(@Qualifier("pgJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<DbInstance> findAllInstances() {
        return jdbc.query("""
                SELECT i.instance_id, i.db_name, i.hostname, i.type,
                       i.port, i.db_version
                FROM xm_instance i
                WHERE i.deleted = false
                ORDER BY i.db_name
                """,
                (rs, rowNum) -> DbInstance.builder()
                        .instanceId(rs.getString("instance_id"))
                        .instanceName(rs.getString("db_name"))
                        .dbType(rs.getString("type"))
                        .host(rs.getString("hostname"))
                        .port(rs.getObject("port") != null ? rs.getInt("port") : null)
                        .version(rs.getString("db_version"))
                        .build());
    }

    public DbInstance findInstanceById(String instanceId) {
        List<DbInstance> results = jdbc.query("""
                SELECT i.instance_id, i.db_name, i.hostname, i.type,
                       i.port, i.db_version
                FROM xm_instance i
                WHERE i.instance_id = ? AND i.deleted = false
                """,
                (rs, rowNum) -> DbInstance.builder()
                        .instanceId(rs.getString("instance_id"))
                        .instanceName(rs.getString("db_name"))
                        .dbType(rs.getString("type"))
                        .host(rs.getString("hostname"))
                        .port(rs.getObject("port") != null ? rs.getInt("port") : null)
                        .version(rs.getString("db_version"))
                        .build(),
                instanceId);
        return results.isEmpty() ? null : results.get(0);
    }
}
