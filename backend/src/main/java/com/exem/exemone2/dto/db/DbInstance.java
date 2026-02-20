package com.exem.exemone2.dto.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbInstance {
    private String instanceId;
    private String instanceName;
    private String dbType;
    private String host;
    private Integer port;
    private String version;
    private String status;
}
