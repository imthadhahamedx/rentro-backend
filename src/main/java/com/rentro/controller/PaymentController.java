package com.rentro.controller;

import com.rentro.dto.request.payment.PayhereHashRequestDto;
import com.rentro.dto.request.payment.PayhereNotifyRequestDto;
import com.rentro.dto.request.payment.PaymentCreateRequestDto;
import com.rentro.dto.request.payment.PaymentRefundRequestDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ─── List payments for a booking ─────────────────────────────────────────
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findByBookingId(@PathVariable String bookingId) {
        return ResponseEntity.ok(StandardResponseDto.builder()
                .code(200)
                .message("Payments fetched successfully")
                .data(paymentService.findByBookingId(bookingId))
                .build());
    }

    // ─── All payments (admin) ────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findAllByStatus(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(StandardResponseDto.builder()
                .code(200)
                .message("Payments fetched successfully")
                .data(paymentService.findAllByStatus(searchText, status, page, size))
                .build());
    }

    // ─── Payment detail ──────────────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(StandardResponseDto.builder()
                .code(200)
                .message("Payment fetched successfully")
                .data(paymentService.findById(id))
                .build());
    }

    // ─── Record cash / manual payment ────────────────────────────────────────
    @PostMapping("/cash")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> recordCash(
            @Valid @RequestBody PaymentCreateRequestDto dto,
            Authentication auth
    ) {
        UUID paymentId = paymentService.recordCashPayment(dto, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(StandardResponseDto.builder()
                .code(201)
                .message("Cash payment recorded successfully")
                .data(paymentId)
                .build());
    }

    // ─── Initiate online (PayHere) payment ───────────────────────────────────
    /**
     * Step 1: Frontend calls this to create a PENDING payment record.
     * Returns the payment UUID → used as PayHere order_id and passed in custom_1.
     */
    @PostMapping("/online/init")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> initOnline(
            @Valid @RequestBody PaymentCreateRequestDto dto,
            Authentication auth
    ) {
        UUID paymentId = paymentService.initOnlinePayment(dto, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(StandardResponseDto.builder()
                .code(201)
                .message("Online payment initiated")
                .data(paymentId)
                .build());
    }

    /**
     * Step 2: Frontend calls this to get the hash needed for PayHere JS SDK.
     */
    @PostMapping("/online/hash")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> getHash(@Valid @RequestBody PayhereHashRequestDto dto) {
        return ResponseEntity.ok(StandardResponseDto.builder()
                .code(200)
                .message("Hash generated")
                .data(paymentService.generatePayhereHash(dto))
                .build());
    }

    // ─── PayHere server-to-server notify (NO auth) ───────────────────────────
    /**
     * PayHere POSTs here after each transaction.
     * Must be publicly accessible – add this path to SecurityConfig permitAll.
     * Always returns HTTP 200 (PayHere requirement).
     */
    @PostMapping("/payhere/notify")
    public ResponseEntity<Void> payhereNotify(PayhereNotifyRequestDto notify) {
        paymentService.handlePayhereNotify(notify);
        return ResponseEntity.ok().build();
    }

    // ─── Refund ──────────────────────────────────────────────────────────────
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StandardResponseDto> refund(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentRefundRequestDto dto,
            Authentication auth
    ) {
        paymentService.refundPayment(id, dto, auth.getName());
        return ResponseEntity.ok(StandardResponseDto.builder()
                .code(200)
                .message("Payment refunded successfully")
                .data(null)
                .build());
    }
}
