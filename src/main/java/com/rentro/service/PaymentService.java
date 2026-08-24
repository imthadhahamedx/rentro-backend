package com.rentro.service;

import com.rentro.dto.request.payment.PayhereHashRequestDto;
import com.rentro.dto.request.payment.PayhereNotifyRequestDto;
import com.rentro.dto.request.payment.PaymentCreateRequestDto;
import com.rentro.dto.request.payment.PaymentRefundRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.payment.PayhereHashResponseDto;
import com.rentro.dto.response.payment.PaymentDetailResponseDto;
import com.rentro.dto.response.payment.PaymentListItemResponseDto;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    /** List all payments for a given booking */
    List<PaymentListItemResponseDto> findByBookingId(String bookingId);

    /** List all payments (admin view) with optional filters */
    PaginatedResponseDto findAllByStatus(String searchText, String status, int page, int size);

    /** Get single payment detail */
    PaymentDetailResponseDto findById(UUID id);

    /**
     * Record a CASH payment immediately as COMPLETED.
     * Also used to record a manual BANK_TRANSFER, etc.
     */
    UUID recordCashPayment(PaymentCreateRequestDto dto, String staffEmail);

    /**
     * Create a PENDING online payment record and return its UUID.
     * Frontend uses this UUID as the PayHere order_id.
     */
    UUID initOnlinePayment(PaymentCreateRequestDto dto, String staffEmail);

    /** Generate the hash for a PayHere checkout request */
    PayhereHashResponseDto generatePayhereHash(PayhereHashRequestDto dto);

    /**
     * Called by PayHere notify webhook (no auth).
     * Validates md5sig and updates payment status to COMPLETED or FAILED.
     */
    void handlePayhereNotify(PayhereNotifyRequestDto notify);

    /** Mark a completed payment as REFUNDED */
    void refundPayment(UUID id, PaymentRefundRequestDto dto, String staffEmail);
}
