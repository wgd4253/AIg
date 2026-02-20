package com.exem.exemone2.repository.redis;

import java.time.Instant;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.app.AppInstanceStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AppRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public AppInstanceStatus findInstanceStatus(String applicationId) {
        String json = redisTemplate.opsForValue().get("ApplicationState." + applicationId);
        if (json == null) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            String agentJson = redisTemplate.opsForValue().get("XmAgentState.java." + applicationId);
            String state = "inactive";
            if (agentJson != null) {
                JsonNode agentNode = objectMapper.readTree(agentJson);
                state = agentNode.has("state") ? agentNode.get("state").asText() : "inactive";
            }
            long connectedTime = node.has("connectedTime") ? node.get("connectedTime").asLong() : 0;
            return AppInstanceStatus.builder()
                    .instanceId(applicationId)
                    .status(state)
                    .lastUpdated(connectedTime > 0 ? Instant.ofEpochMilli(connectedTime) : null)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
