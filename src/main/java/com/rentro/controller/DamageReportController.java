package com.rentro.controller;

import com.rentro.dto.request.damage_report.DamageReportRequestDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.DamageReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Damage Report endpoints — linking a Booking to an existing Damage record
 * and tracking staff review status.
 *
 * GET  /damage-reports/admin         → paginated list (search, reviewed filter)
 * GET  /damage-reports/{id}          → detail
 * POST /damage-reports               → create (link booking + damage)
 * POST /damage-reports/{id}/review   → mark as reviewed by current user
 * DELETE /damage-reports/{id}        → delete (SUPER_ADMIN only)
 */
@RestController
@RequestMapping("/damage-reports")
@RequiredArgsConstructor
public class DamageReportController {

    private final DamageReportService damageReportService;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findAll(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) String reviewed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage reports fetched successfully")
                        .data(damageReportService.search(searchText, reviewed, page, size))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage report fetched successfully")
                        .data(damageReportService.findById(id))
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> create(@Valid @RequestBody DamageReportRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDto.builder()
                        .code(201)
                        .message("Damage report created successfully")
                        .data(damageReportService.create(dto))
                        .build()
        );
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> markAsReviewed(@PathVariable UUID id) {
        damageReportService.markAsReviewed(id);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage report marked as reviewed")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteById(@PathVariable UUID id) {
        damageReportService.deleteById(id);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage report deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
