package com.rentro.controller;

import com.rentro.dto.request.booking.*;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
public class BookingController {

    private final BookingService bookingService;

    // ─── List / detail ──────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<StandardResponseDto> findAll(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Bookings fetched successfully")
                        .data(bookingService.findAll(searchText, status, page, size))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Booking fetched successfully")
                        .data(bookingService.findById(id))
                        .build()
        );
    }

    @GetMapping("/payment/{ref}")
    public ResponseEntity<StandardResponseDto> findByRef(@PathVariable String ref) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Booking fetched successfully")
                        .data(bookingService.findByRef(ref))
                        .build()
        );
    }

    // ─── Options for the "New booking" form ────────────────────────────────
    @GetMapping("/options/customers")
    public ResponseEntity<StandardResponseDto> customerOptions(
            @RequestParam(required = false, defaultValue = "") String searchText
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Customer options fetched successfully")
                        .data(bookingService.findCustomerOptions(searchText))
                        .build()
        );
    }

    @GetMapping("/options/vehicles")
    public ResponseEntity<StandardResponseDto> vehicleOptions(
            @RequestParam(required = false, defaultValue = "") String searchText
    ) {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle options fetched successfully")
                        .data(bookingService.findAvailableVehicleOptions(searchText))
                        .build()
        );
    }

    @GetMapping("/options/locations")
    public ResponseEntity<StandardResponseDto> locationOptions() {
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Location options fetched successfully")
                        .data(bookingService.findLocationOptions())
                        .build()
        );
    }

    // ─── Create ─────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<StandardResponseDto> create(@Valid @RequestBody BookingCreateRequestDto dto) {
        UUID id = bookingService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponseDto.builder()
                        .code(201)
                        .message("Booking created successfully")
                        .data(id)
                        .build()
        );
    }

    // ─── Lifecycle actions ──────────────────────────────────────────────────
    @PutMapping("/{id}/confirm")
    public ResponseEntity<StandardResponseDto> confirm(@PathVariable UUID id) {
        bookingService.confirm(id);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Booking confirmed successfully")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<StandardResponseDto> activate(@PathVariable UUID id) {
        bookingService.activate(id);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle picked up — booking is now active")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<StandardResponseDto> complete(
            @PathVariable UUID id,
            @Valid @RequestBody BookingCompleteRequestDto dto
    ) {
        bookingService.complete(id, dto);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Vehicle returned — booking completed")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<StandardResponseDto> cancel(
            @PathVariable UUID id,
            @Valid @RequestBody BookingCancelRequestDto dto
    ) {
        bookingService.cancel(id, dto);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Booking cancelled successfully")
                        .data(null)
                        .build()
        );
    }

    @PostMapping("/{id}/extension")
    public ResponseEntity<StandardResponseDto> extend(
            @PathVariable UUID id,
            @Valid @RequestBody BookingExtensionRequestDto dto
    ) {
        bookingService.extend(id, dto);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Booking extended successfully")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/{id}/notes")
    public ResponseEntity<StandardResponseDto> updateNotes(
            @PathVariable UUID id,
            @Valid @RequestBody BookingNotesRequestDto dto
    ) {
        bookingService.updateNotes(id, dto);
        return ResponseEntity.ok(
                StandardResponseDto.builder()
                        .code(200)
                        .message("Notes updated successfully")
                        .data(null)
                        .build()
        );
    }
}
