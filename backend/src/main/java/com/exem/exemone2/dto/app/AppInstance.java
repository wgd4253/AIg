package com.exem.exemone2.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInstance {
    private String instanceId;
    private String instanceName;
    private String groupId;
    private String host;
    private Integer port;
    private String wasType;
    private String status;
}
