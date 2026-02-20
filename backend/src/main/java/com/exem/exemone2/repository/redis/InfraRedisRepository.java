package com.exem.exemone2.repository.redis;

import java.time.Instant;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.exem.exemone2.dto.infra.HostStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class InfraRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public HostStatus findHostStatus(String hostId) {
        String json = redisTemplate.opsForValue().get("InfraState." + hostId);
        if (json == null) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            String agentJson = redisTemplate.opsForValue().get("XmAgentState.host." + hostId);
            String state = "inactive";
            if (agentJson != null) {
                JsonNode agentNode = objectMapper.readTree(agentJson);
                state = agentNode.has("state") ? agentNode.get("state").asText() : "inactive";
            }
            long connectedTime = node.has("connectedTime") ? node.get("connectedTime").asLong() : 0;
            return HostStatus.builder()
                    .hostKey(hostId)
                    .status(state)
                    .lastUpdated(connectedTime > 0 ? Instant.ofEpochMilli(connectedTime) : null)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
