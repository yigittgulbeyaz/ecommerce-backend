package com.yigit.ecommerce.controller.admin;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.response.admin.AdminStatsResponse;
import com.yigit.ecommerce.service.admin.AdminStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/stats")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        AdminStatsResponse stats = adminStatsService.getStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Dashboard stats fetched"));
    }
}
