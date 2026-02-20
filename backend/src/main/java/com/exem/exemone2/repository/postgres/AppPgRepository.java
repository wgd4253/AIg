package com.exem.exemone2.repository.postgres;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.app.AppGroup;
import com.exem.exemone2.dto.app.AppInstance;

@Repository
public class AppPgRepository {

    private final JdbcTemplate jdbc;

    public AppPgRepository(@Qualifier("pgJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<AppGroup> findAllGroups() {
        return jdbc.query("""
                SELECT ag.application_group_id, ag.group_name,
                       COUNT(a.application_id) AS instance_count
                FROM xm_application_group ag
                LEFT JOIN xm_application a ON a.application_group_id = ag.application_group_id
                    AND a.deleted = false
                WHERE ag.deleted = false
                GROUP BY ag.application_group_id, ag.group_name
                ORDER BY ag.group_name
                """,
                (rs, rowNum) -> AppGroup.builder()
                        .groupId(rs.getString("application_group_id"))
                        .groupName(rs.getString("group_name"))
                        .instanceCount(rs.getInt("instance_count"))
                        .build());
    }

    public List<AppInstance> findInstancesByGroupId(String groupId) {
        return jdbc.query("""
                SELECT a.application_id, a.name, a.application_group_id,
                       a.hostname, a.type, a.platform
                FROM xm_application a
                WHERE a.application_group_id = ? AND a.deleted = false
                ORDER BY a.name
                """,
                (rs, rowNum) -> AppInstance.builder()
                        .instanceId(rs.getString("application_id"))
                        .instanceName(rs.getString("name"))
                        .groupId(rs.getString("application_group_id"))
                        .host(rs.getString("hostname"))
                        .wasType(rs.getString("type"))
                        .build(),
                groupId);
    }

    public AppInstance findInstanceById(String instanceId) {
        List<AppInstance> results = jdbc.query("""
                SELECT a.application_id, a.name, a.application_group_id,
                       a.hostname, a.type, a.platform
                FROM xm_application a
                WHERE a.application_id = ? AND a.deleted = false
                """,
                (rs, rowNum) -> AppInstance.builder()
                        .instanceId(rs.getString("application_id"))
                        .instanceName(rs.getString("name"))
                        .groupId(rs.getString("application_group_id"))
                        .host(rs.getString("hostname"))
                        .wasType(rs.getString("type"))
                        .build(),
                instanceId);
        return results.isEmpty() ? null : results.get(0);
    }
}
