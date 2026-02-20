package com.exem.exemone2.controller.db;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exem.exemone2.dto.common.ApiResponse;
import com.exem.exemone2.dto.common.TimeRange;
import com.exem.exemone2.dto.db.DbSqlDetail;
import com.exem.exemone2.dto.db.DbSqlPlan;
import com.exem.exemone2.dto.db.DbSqlSummary;
import com.exem.exemone2.service.db.DbSqlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/db")
@RequiredArgsConstructor
public class DbSqlController {

    private final DbSqlService dbSqlService;

    @GetMapping("/instances/{instanceId}/sql/top")
    public ApiResponse<List<DbSqlSummary>> getTopSql(@PathVariable String instanceId,
                                                      TimeRange range,
                                                      @RequestParam(defaultValue = "10") int top) {
        return ApiResponse.ok(dbSqlService.getTopSql(instanceId, range, top));
    }

    @GetMapping("/sql/{sqlId}")
    public ApiResponse<DbSqlDetail> getSqlDetail(@PathVariable String sqlId) {
        return ApiResponse.ok(dbSqlService.getSqlDetail(sqlId));
    }

    @GetMapping("/sql/{sqlId}/plan")
    public ApiResponse<DbSqlPlan> getSqlPlan(@PathVariable String sqlId) {
        return ApiResponse.ok(dbSqlService.getSqlPlan(sqlId));
    }
}
