package com.rentro.controller;

import com.rentro.dto.request.damage.DamageFixedUpdateRequestDto;
import com.rentro.dto.request.damage.DamageRequestDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.DamageService;
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
 * Vehicle damage management endpoints.
 * <p>
 * Create / update accept multipart/form-data with two parts:
 *   - "damage": JSON body matching {@link DamageRequestDto} (Content-Type: application/json)
 *   - "images": zero or more image files (jpeg/png/webp, up to aws.s3.max-file-size-mb each)
 */
@RestController
@RequestMapping("/damage")
@RequiredArgsConstructor
public class DamageController {

    private final DamageService damageService;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findAllForAdmin(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String damageBy,
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage records fetched successfully")
                        .data(damageService.search(searchText, status, damageBy, vehicleId, page, size))
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage record fetched successfully")
                        .data(damageService.findById(id))
                        .build()
        );
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> create(
            @Valid @RequestPart("damage") DamageRequestDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDto.builder()
                        .code(201)
                        .message("Damage record created successfully")
                        .data(damageService.create(dto, images))
                        .build()
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestPart("damage") DamageRequestDto dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage record updated successfully")
                        .data(damageService.update(id, dto, newImages))
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> updateFixedStatus(
            @PathVariable UUID id,
            @Valid @RequestBody DamageFixedUpdateRequestDto dto
    ) {
        damageService.updateFixedStatus(id, dto);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage status updated successfully")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteById(@PathVariable UUID id) {
        damageService.deleteById(id);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage record deleted successfully")
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
                        .message("Damage images uploaded successfully")
                        .data(damageService.addImages(id, images))
                        .build()
        );
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteImage(
            @PathVariable UUID id,
            @PathVariable UUID imageId
    ) {
        damageService.deleteImage(id, imageId);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Damage image deleted successfully")
                        .data(null)
                        .build()
        );
    }
}
