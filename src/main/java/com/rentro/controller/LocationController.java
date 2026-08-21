package com.rentro.controller;

import com.rentro.dto.request.location.LocationRequestDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findAllForAdmin(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Locations fetched successfully")
                        .data(locationService.findAllForAdmin(searchText, isActive, page, size))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Location fetched successfully")
                        .data(locationService.findById(id))
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> create(@Valid @RequestBody LocationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDto.builder()
                        .code(201)
                        .message("Location created successfully")
                        .data(locationService.create(dto))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody LocationRequestDto dto
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Location updated successfully")
                        .data(locationService.update(id, dto))
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body
    ) {
        boolean isActive = Boolean.TRUE.equals(body.get("isActive"));
        locationService.updateStatus(id, isActive);

        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Location status updated successfully")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteById(@PathVariable UUID id) {
        locationService.deleteById(id);

        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Location deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
