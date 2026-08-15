package com.rentro.controller;

import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> getOverview() {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Dashboard overview fetched successfully")
                        .data(dashboardService.getOverview())
                        .build()
        );
    }
}
