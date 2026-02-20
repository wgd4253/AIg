package com.exem.exemone2.repository.redis;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.db.DbInstanceStatus;
import com.exem.exemone2.dto.db.DbSession;
import com.exem.exemone2.dto.db.LockTreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DbRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public DbInstanceStatus findInstanceStatus(String instanceId) {
        String json = redisTemplate.opsForValue().get("InstanceState." + instanceId);
        if (json == null) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            String dbState = node.has("dbState") ? node.get("dbState").asText() : "inactive";
            long connectedTime = node.has("connectedTime") ? node.get("connectedTime").asLong() : 0;
            return DbInstanceStatus.builder()
                    .instanceId(instanceId)
                    .status(dbState)
                    .lastUpdated(connectedTime > 0 ? Instant.ofEpochMilli(connectedTime) : null)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public List<DbSession> findActiveSessions(String instanceId) {
        String json = redisTemplate.opsForValue().get(".srs" + instanceId);
        if (json == null) {
            return List.of();
        }
        try {
            JsonNode root = objectMapper.readTree(json);
            List<DbSession> sessions = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode node : root) {
                    sessions.add(DbSession.builder()
                            .sessionId(node.has("sessionId") ? node.get("sessionId").asText() : null)
                            .username(node.has("username") ? node.get("username").asText() : null)
                            .status(node.has("status") ? node.get("status").asText() : null)
                            .sqlText(node.has("sqlText") ? node.get("sqlText").asText() : null)
                            .waitEvent(node.has("waitEvent") ? node.get("waitEvent").asText() : null)
                            .elapsedTime(node.has("elapsedTime") ? node.get("elapsedTime").asLong() : null)
                            .build());
                }
            }
            return sessions;
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<LockTreeNode> findLockTree(String instanceId) {
        // Lock tree data is typically from ClickHouse postgresql_locktree
        // Redis doesn't have structured lock tree, return empty
        return List.of();
    }
}
