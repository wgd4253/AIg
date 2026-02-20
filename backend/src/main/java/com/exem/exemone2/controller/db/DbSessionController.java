package com.exem.exemone2.controller.db;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exem.exemone2.dto.common.ApiResponse;
import com.exem.exemone2.dto.db.DbSession;
import com.exem.exemone2.dto.db.LockTreeNode;
import com.exem.exemone2.service.db.DbSessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/db/instances/{instanceId}/sessions")
@RequiredArgsConstructor
public class DbSessionController {

    private final DbSessionService dbSessionService;

    @GetMapping("/active")
    public ApiResponse<List<DbSession>> getActiveSessions(@PathVariable String instanceId) {
        return ApiResponse.ok(dbSessionService.getActiveSessions(instanceId));
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<DbSession> getSession(@PathVariable String instanceId,
                                              @PathVariable String sessionId) {
        return ApiResponse.ok(dbSessionService.getSession(instanceId, sessionId));
    }

    @GetMapping("/lock-tree")
    public ApiResponse<List<LockTreeNode>> getLockTree(@PathVariable String instanceId) {
        return ApiResponse.ok(dbSessionService.getLockTree(instanceId));
    }
}
