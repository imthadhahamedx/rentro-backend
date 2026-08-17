package com.rentro.controller;

import com.rentro.dto.request.vehicleCategory.VehicleCategoryRequestDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.VehicleCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/vehicle-categories")
@RequiredArgsConstructor
public class VehicleCategoryController {

    private final VehicleCategoryService vehicleCategoryService;

    @GetMapping("/public/search")
    public ResponseEntity<StandardResponseDto> search(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle categories fetched successfully")
                        .data(vehicleCategoryService.search(searchText, page, size))
                        .build()
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findAllForAdmin(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle categories fetched successfully")
                        .data(vehicleCategoryService.findAllForAdmin(searchText, page, size))
                        .build()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> create(
            @Valid @RequestBody VehicleCategoryRequestDto dto
    ) {

        vehicleCategoryService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        StandardResponseDto.builder()
                                .code(201)
                                .message("Vehicle category created successfully")
                                .data(null)
                                .build()
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleCategoryRequestDto dto
    ) {

        vehicleCategoryService.update(id, dto);

        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle category updated successfully")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteById(
            @PathVariable UUID id
    ) {

        vehicleCategoryService.deleteById(id);

        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle category deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
