package com.rentro.controller;

import com.rentro.dto.request.customer.CustomerCreateRequestDto;
import com.rentro.dto.request.customer.CustomerStatusUpdateRequestDto;
import com.rentro.dto.request.customer.CustomerUpdateRequestDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Customer management endpoints (staff/admin dashboard).
 * <p>
 * Registers and maintains customer accounts — each customer is backed by a
 * {@code User} (role = CUSTOMER) plus the {@code Customer} profile (NIC,
 * driving licence, address, notes).
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerController {

    private final CustomerService customerService;

    // ─── List / detail ──────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<StandardResponseDto> findAll(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Customers fetched successfully")
                        .data(customerService.search(searchText, isActive, page, size))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Customer fetched successfully")
                        .data(customerService.findById(id))
                        .build()
        );
    }

    // ─── Create / update ────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<StandardResponseDto> create(@Valid @RequestBody CustomerCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDto.builder()
                        .code(201)
                        .message("Customer created successfully")
                        .data(customerService.create(dto))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerUpdateRequestDto dto
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Customer updated successfully")
                        .data(customerService.update(id, dto))
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StandardResponseDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerStatusUpdateRequestDto dto
    ) {
        customerService.updateStatus(id, dto);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Customer status updated successfully")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> deleteById(@PathVariable UUID id) {
        customerService.deleteById(id);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Customer deleted successfully")
                        .data(null)
                        .build()
        );
    }

    // ─── Booking history ────────────────────────────────────────────────────
    @GetMapping("/{id}/bookings")
    public ResponseEntity<StandardResponseDto> findBookings(
            @PathVariable UUID id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Customer bookings fetched successfully")
                        .data(customerService.findBookings(id, status, page, size))
                        .build()
        );
    }
}
