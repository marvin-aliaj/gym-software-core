package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.DashboardResponse;
import com.spring.jdbc.gym.management.model.filter.DashboardFilter;
import com.spring.jdbc.gym.management.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @RequiresRole({"ADMIN", "BUSINESS_OWNER", "BUSINESS_MANAGER"})
    @GetMapping("/metrics")
    public ResponseEntity<Object> getDashboardMetrics(
            @RequestParam(defaultValue = "7") int period,
            @RequestParam(required = false) String businessId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            DashboardFilter filter = new DashboardFilter(period, businessId, startDate, endDate);
            DashboardResponse response = dashboardService.getDashboardMetrics(filter);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
