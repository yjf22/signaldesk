package com.signaldesk.dashboard.controller;

import com.signaldesk.dashboard.dto.DashboardStatsResponse;
import com.signaldesk.dashboard.dto.RecentChangeResponse;
import com.signaldesk.dashboard.service.DashboardService;
import com.signaldesk.infrastructure.dto.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsResponse> getStats(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(dashboardService.getStats(userId));
    }

    @GetMapping("/recent-changes")
    public ApiResponse<List<RecentChangeResponse>> getRecentChanges(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResponse.success(dashboardService.getRecentChanges(userId));
    }
}
