package com.exem.exemone2.controller.app;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exem.exemone2.dto.app.AppGroup;
import com.exem.exemone2.dto.app.AppInstance;
import com.exem.exemone2.dto.common.ApiResponse;
import com.exem.exemone2.service.app.AppGroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppGroupController {

    private final AppGroupService appGroupService;

    @GetMapping("/groups")
    public ApiResponse<List<AppGroup>> getGroups() {
        return ApiResponse.ok(appGroupService.getGroups());
    }

    @GetMapping("/groups/{groupId}/instances")
    public ApiResponse<List<AppInstance>> getInstances(@PathVariable String groupId) {
        return ApiResponse.ok(appGroupService.getInstances(groupId));
    }
}
