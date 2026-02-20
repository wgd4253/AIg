package com.exem.exemone2.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppGroup {
    private String groupId;
    private String groupName;
    private Integer instanceCount;
    private String status;
}
