package com.exem.exemone2.service.db;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.db.DbSession;
import com.exem.exemone2.dto.db.LockTreeNode;
import com.exem.exemone2.repository.clickhouse.DbChRepository;
import com.exem.exemone2.repository.redis.DbRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DbSessionService {

    private final DbChRepository chRepo;
    private final DbRedisRepository redisRepo;

    public List<DbSession> getActiveSessions(String instanceId) {
        // Try Redis first for real-time data, fall back to ClickHouse
        List<DbSession> sessions = redisRepo.findActiveSessions(instanceId);
        if (sessions.isEmpty()) {
            sessions = chRepo.findActiveSessions(instanceId);
        }
        return sessions;
    }

    public DbSession getSession(String instanceId, String sessionId) {
        return chRepo.findSession(instanceId, sessionId);
    }

    public List<LockTreeNode> getLockTree(String instanceId) {
        return redisRepo.findLockTree(instanceId);
    }
}
