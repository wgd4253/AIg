package com.exem.exemone2.service.db;

import java.util.List;

import org.springframework.stereotype.Service;

import com.exem.exemone2.dto.db.DbSession;
import com.exem.exemone2.dto.db.LockTreeNode;

@Service
public class DbSessionService {

    public List<DbSession> getActiveSessions(String instanceId) {
        // TODO: Redis 또는 ClickHouse에서 액티브 세션 목록 조회
        return List.of();
    }

    public DbSession getSession(String instanceId, String sessionId) {
        // TODO: Redis 또는 ClickHouse에서 세션 상세 조회
        return null;
    }

    public List<LockTreeNode> getLockTree(String instanceId) {
        // TODO: Redis 또는 ClickHouse에서 락/블로킹 트리 조회
        return List.of();
    }
}
