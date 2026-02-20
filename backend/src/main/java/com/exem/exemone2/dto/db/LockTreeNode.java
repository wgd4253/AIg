package com.exem.exemone2.dto.db;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockTreeNode {
    private String sessionId;
    private String username;
    private String lockType;
    private String lockMode;
    private String sqlText;
    private List<LockTreeNode> waiters;
}
