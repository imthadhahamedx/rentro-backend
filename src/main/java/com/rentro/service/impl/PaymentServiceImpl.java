package com.rentro.service.impl;

import com.rentro.dto.request.payment.PayhereHashRequestDto;
import com.rentro.dto.request.payment.PayhereNotifyRequestDto;
import com.rentro.dto.request.payment.PaymentCreateRequestDto;
import com.rentro.dto.request.payment.PaymentRefundRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.booking.BookingListItemResponseDto;
import com.rentro.dto.response.payment.PayhereHashResponseDto;
import com.rentro.dto.response.payment.PaymentDetailResponseDto;
import com.rentro.dto.response.payment.PaymentListItemResponseDto;
import com.rentro.entity.BookingEntity;
import com.rentro.entity.PaymentEntity;
import com.rentro.entity.UserEntity;
import com.rentro.entity.VehicleEntity;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.BookingRepository;
import com.rentro.repository.PaymentRepository;
import com.rentro.repository.UserRepository;
import com.rentro.service.PaymentService;
import com.rentro.util.PayhereUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PayhereUtil payhereUtil;

    // ─── Reads ───────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<PaymentListItemResponseDto> findByBookingId(String bookingId) {
        BookingEntity bookingEntity = bookingRepository.findByBookingRef(bookingId)
                .orElseThrow(() -> new EntryNotFoundException("Booking not found"));

        return bookingEntity.getPayments().stream()
                .map(this::toPaymentListItemResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto findAllByStatus(String searchText, String status, int page, int size) {
        // Simple implementation – extend with Specification if needed
        PaymentEntity.PaymentStatus paymentStatus = null;

        if (status != null && !status.isBlank()) {
            paymentStatus = PaymentEntity.PaymentStatus.valueOf(status);
        }

        String text = searchText == null ? "" : searchText.trim();
        Page<PaymentEntity> result = paymentRepository.findAllByStatus(text, paymentStatus, PageRequest.of(page, size));

        return PaginatedResponseDto.<PaymentListItemResponseDto>builder()
                .count(result.getTotalElements())
                .dataList(result.getContent().stream().map(this::toPaymentListItemResponseDto).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDetailResponseDto findById(UUID id) {
        PaymentEntity paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Payment not found"));
        return toPaymentDetailResponseDto(paymentEntity);
    }

    // ─── Cash / manual payment ────────────────────────────────────────────────
    @Override
    public UUID recordCashPayment(PaymentCreateRequestDto dto, String staffEmail) {
        BookingEntity bookingEntity = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntryNotFoundException("Booking not found"));

        UserEntity staff = userRepository.findUserEntityByEmail(staffEmail)
                .orElseThrow(() -> new EntryNotFoundException("Staff not found"));

        String ref = generatePaymentRef();

        PaymentEntity paymentEntity = PaymentEntity.builder()
                .paymentRef(ref)
                .amount(dto.getAmount())
                .paymentMethod(parseMethod(dto.getPaymentMethod()))
                .paymentType(parseType(dto.getPaymentType()))
                .status(PaymentEntity.PaymentStatus.COMPLETED)
                .transactionId(dto.getTransactionId())
                .notes(dto.getNotes())
                .paidAt(LocalDateTime.now())
                .booking(bookingEntity)
                .processedBy(staff)
                .build();

        paymentRepository.save(paymentEntity);
        log.info("Cash payment {} recorded for booking {}", ref, bookingEntity.getBookingRef());
        return paymentEntity.getId();
    }

    // ─── Online / PayHere flow ────────────────────────────────────────────────
    @Override
    public UUID initOnlinePayment(PaymentCreateRequestDto dto, String staffEmail) {
        BookingEntity bookingEntity = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntryNotFoundException("Booking not found"));

        UserEntity staff = userRepository.findUserEntityByEmail(staffEmail)
                .orElseThrow(() -> new EntryNotFoundException("Staff not found"));

        String ref = generatePaymentRef();

        PaymentEntity paymentEntity = PaymentEntity.builder()
                .paymentRef(ref)
                .amount(dto.getAmount())
                .paymentMethod(PaymentEntity.PaymentMethod.ONLINE)
                .paymentType(parseType(dto.getPaymentType()))
                .status(PaymentEntity.PaymentStatus.PENDING)
                .notes(dto.getNotes())
                .booking(bookingEntity)
                .processedBy(staff)
                .build();

        paymentRepository.save(paymentEntity);
        log.info("Online payment {} initialised for booking {}", ref, bookingEntity.getBookingRef());
        return paymentEntity.getId();
    }

    @Override
    public PayhereHashResponseDto generatePayhereHash(PayhereHashRequestDto dto) {
        String hash = payhereUtil.generateHash(dto.getOrderId(), dto.getAmount(), dto.getCurrency());
        return PayhereHashResponseDto.builder()
                .merchantId(payhereUtil.getMerchantId())
                .hash(hash)
                .build();
    }

    /**
     * PayHere posts to this endpoint after a card transaction.
     * custom_1 contains our payment UUID (set by the frontend when building the checkout).
     */
    @Override
    public void handlePayhereNotify(PayhereNotifyRequestDto notify) {
        log.info("PayHere notify received: order={} status={}", notify.getOrder_id(), notify.getStatus_code());

        boolean valid = payhereUtil.verifyNotification(
                notify.getOrder_id(),
                notify.getPayhere_amount(),
                notify.getPayhere_currency(),
                notify.getStatus_code(),
                notify.getMd5sig()
        );

        if (!valid) {
            log.warn("PayHere notify INVALID signature for order {}", notify.getOrder_id());
            return; // silently discard – PayHere expects HTTP 200 regardless
        }

        // custom_1 = payment UUID
        UUID paymentId;
        try {
            paymentId = UUID.fromString(notify.getCustom_1());
        } catch (Exception e) {
            log.warn("PayHere notify: invalid custom_1 (payment UUID) = {}", notify.getCustom_1());
            return;
        }

        PaymentEntity paymentEntity = paymentRepository.findById(paymentId).orElse(null);
        if (paymentEntity == null) {
            log.warn("PayHere notify: payment {} not found", paymentId);
            return;
        }

        int statusCode = Integer.parseInt(notify.getStatus_code());

        if (statusCode == 2) {
            // SUCCESS
            paymentEntity.setStatus(PaymentEntity.PaymentStatus.COMPLETED);
            paymentEntity.setTransactionId(notify.getPayment_id());
            paymentEntity.setGatewayResponse(buildGatewayResponse(notify));
            paymentEntity.setPaidAt(LocalDateTime.now());
            log.info("Payment {} marked COMPLETED (txn={})", paymentId, notify.getPayment_id());
        } else if (statusCode == -1 || statusCode == -2 || statusCode == -3) {
            // CANCELED / FAILED / CHARGEBACK
            paymentEntity.setStatus(PaymentEntity.PaymentStatus.FAILED);
            paymentEntity.setGatewayResponse(buildGatewayResponse(notify));
            log.warn("Payment {} marked FAILED (status_code={})", paymentId, statusCode);
        }
        // status 0 (PENDING) – leave as PENDING

        paymentRepository.save(paymentEntity);
    }

    // ─── Refund ───────────────────────────────────────────────────────────────
    @Override
    public void refundPayment(UUID id, PaymentRefundRequestDto dto, String staffEmail) {
        PaymentEntity paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Payment not found"));

        if (paymentEntity.getStatus() != PaymentEntity.PaymentStatus.COMPLETED) {
            throw new ValidationException("Only COMPLETED payments can be refunded");
        }

        paymentEntity.setStatus(PaymentEntity.PaymentStatus.REFUNDED);
        paymentEntity.setNotes((paymentEntity.getNotes() != null ? paymentEntity.getNotes() + " | " : "") + "Refund: " + dto.getReason());
        paymentRepository.save(paymentEntity);
        log.info("Payment {} refunded. Reason: {}", paymentEntity.getPaymentRef(), dto.getReason());
    }

    // ─── Mappers ──────────────────────────────────────────────────────────────
    private PaymentListItemResponseDto toPaymentListItemResponseDto(PaymentEntity p) {
        BookingEntity bookingEntity = p.getBooking();
        UserEntity cu = bookingEntity != null && bookingEntity.getCustomer() != null ? bookingEntity.getCustomer().getUser() : null;
        VehicleEntity vehicleEntity = bookingEntity != null ? bookingEntity.getVehicle() : null;
        return PaymentListItemResponseDto.builder()
                .id(p.getId().toString())
                .paymentRef(p.getPaymentRef())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null)
                .paymentType(p.getPaymentType() != null ? p.getPaymentType().name() : null)
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .transactionId(p.getTransactionId())
                .notes(p.getNotes())
                .paidAt(p.getPaidAt())
                .createdAt(p.getCreatedAt())
                .bookingId(bookingEntity != null ? bookingEntity.getId().toString() : null)
                .bookingRef(bookingEntity != null ? bookingEntity.getBookingRef() : null)
                .customerName(cu != null ? cu.getFullName() : null)
                .vehicleName(vehicleEntity != null ? (vehicleEntity.getMake() + " " + vehicleEntity.getModel()) : null)
                .processedByName(p.getProcessedBy() != null ? p.getProcessedBy().getFullName() : null)
                .build();
    }

    private PaymentDetailResponseDto toPaymentDetailResponseDto(PaymentEntity paymentEntity) {
        BookingEntity bookingEntity = paymentEntity.getBooking();
        UserEntity cu = bookingEntity != null && bookingEntity.getCustomer() != null ? bookingEntity.getCustomer().getUser() : null;
        VehicleEntity vehicleEntity = bookingEntity != null ? bookingEntity.getVehicle() : null;

        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal balanceDue = BigDecimal.ZERO;

        if (bookingEntity != null && bookingEntity.getPayments() != null) {
            totalPaid = bookingEntity.getPayments().stream()
                    .filter(x -> x.getStatus() == PaymentEntity.PaymentStatus.COMPLETED
                            && x.getPaymentType() != PaymentEntity.PaymentType.REFUND)
                    .map(PaymentEntity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal finalAmt = bookingEntity.getFinalAmount() != null ? bookingEntity.getFinalAmount() : bookingEntity.getTotalAmount();
            balanceDue = finalAmt != null ? finalAmt.subtract(totalPaid) : BigDecimal.ZERO;
        }

        return PaymentDetailResponseDto.builder()
                .id(paymentEntity.getId().toString())
                .paymentRef(paymentEntity.getPaymentRef())
                .amount(paymentEntity.getAmount())
                .paymentMethod(paymentEntity.getPaymentMethod() != null ? paymentEntity.getPaymentMethod().name() : null)
                .paymentType(paymentEntity.getPaymentType() != null ? paymentEntity.getPaymentType().name() : null)
                .status(paymentEntity.getStatus() != null ? paymentEntity.getStatus().name() : null)
                .transactionId(paymentEntity.getTransactionId())
                .gatewayResponse(paymentEntity.getGatewayResponse())
                .notes(paymentEntity.getNotes())
                .paidAt(paymentEntity.getPaidAt())
                .createdAt(paymentEntity.getCreatedAt())
                .bookingId(bookingEntity != null ? bookingEntity.getId().toString() : null)
                .bookingRef(bookingEntity != null ? bookingEntity.getBookingRef() : null)
                .customerName(cu != null ? cu.getFullName() : null)
                .customerPhoneNumber(cu != null ? cu.getPhoneNumber() : null)
                .vehicleName(vehicleEntity != null ? (vehicleEntity.getMake() + " " + vehicleEntity.getModel()) : null)
                .vehicleRegNo(vehicleEntity != null ? vehicleEntity.getRegNo() : null)
                .bookingFinalAmount(bookingEntity != null ? bookingEntity.getFinalAmount() : null)
                .bookingTotalPaid(totalPaid)
                .bookingBalanceDue(balanceDue)
                .processedByName(paymentEntity.getProcessedBy() != null ? paymentEntity.getProcessedBy().getFullName() : null)
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private String generatePaymentRef() {
        // Format: PAY-XXXXXXXX (8 hex chars from UUID)
        return "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private PaymentEntity.PaymentMethod parseMethod(String method) {
        try {
            return PaymentEntity.PaymentMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Invalid payment method: " + method);
        }
    }

    private PaymentEntity.PaymentType parseType(String type) {
        try {
            return PaymentEntity.PaymentType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Invalid payment type: " + type);
        }
    }

    private String buildGatewayResponse(PayhereNotifyRequestDto n) {
        return String.format(
                "{\"payment_id\":\"%s\",\"method\":\"%s\",\"status_code\":\"%s\",\"status_message\":\"%s\",\"card_no\":\"%s\"}",
                n.getPayment_id(), n.getMethod(), n.getStatus_code(), n.getStatus_message(), n.getCard_no()
        );
    }
}
