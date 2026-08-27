package com.rentro.controller;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.request.vehicle.VehicleStatusUpdateRequestDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Vehicle management endpoints.
 * <p>
 * Create / update accept multipart/form-data with two parts:
 *   - "vehicle": JSON body matching {@link VehicleRequestDto} (Content-Type: application/json)
 *   - "images":  zero or more image files (jpeg/png/webp, up to aws.s3.max-file-size-mb each)
 */

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // ─── public / visitor browsing ───────────────────────────────────────
    @GetMapping("/public/search")
    public ResponseEntity<StandardResponseDto> search(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicles fetched successfully")
                        .data(vehicleService.search(searchText, categoryId, "AVAILABLE", page, size))
                        .build()
        );
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<StandardResponseDto> findByIdPublic(@PathVariable UUID id) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle fetched successfully")
                        .data(vehicleService.findById(id))
                        .build()
        );
    }

    // ─── admin / staff management ────────────────────────────────────────
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findAllForAdmin(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicles fetched successfully")
                        .data(vehicleService.search(searchText, categoryId, status, page, size))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle fetched successfully")
                        .data(vehicleService.findById(id))
                        .build()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> create(
            @Valid @RequestPart("vehicle") VehicleRequestDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDto.builder()
                        .code(201)
                        .message("Vehicle created successfully")
                        .data(vehicleService.create(dto, images))
                        .build()
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestPart("vehicle") VehicleRequestDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle updated successfully")
                        .data(vehicleService.update(id, dto, newImages))
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleStatusUpdateRequestDto dto
    ) {
        vehicleService.updateStatus(id, dto);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle status updated successfully")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteById(@PathVariable UUID id) {
        vehicleService.deleteById(id);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle deleted successfully")
                        .data(null)
                        .build()
        );
    }

    // ─── image management ────────────────────────────────────────────────
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> addImages(
            @PathVariable UUID id,
            @RequestPart("images") List<MultipartFile> images
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDto.builder()
                        .code(201)
                        .message("Vehicle images uploaded successfully")
                        .data(vehicleService.addImages(id, images))
                        .build()
        );
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteImage(
            @PathVariable UUID id,
            @PathVariable UUID imageId
    ) {
        vehicleService.deleteImage(id, imageId);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle image deleted successfully")
                        .data(null)
                        .build()
        );
    }

    @PatchMapping("/{id}/images/{imageId}/primary")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> setPrimaryImage(
            @PathVariable UUID id,
            @PathVariable UUID imageId
    ) {
        vehicleService.setPrimaryImage(id, imageId);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Primary image updated successfully")
                        .data(null)
                        .build()
        );
    }
}
