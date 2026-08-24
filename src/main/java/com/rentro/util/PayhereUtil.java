package com.rentro.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Utility for PayHere hash generation and server-side notification verification.
 *
 * Hash algorithm (PayHere docs):
 *   hash = strtoupper(md5(
 *       merchant_id + order_id + amount_formatted + currency + strtoupper(md5(merchant_secret))
 *   ))
 */
@Component
public class PayhereUtil {

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

    // ─── Hash generation (used when initiating checkout) ──────────────────────
    /**
     * Generate the hash value to pass to PayHere JS SDK.
     *
     * @param orderId   Unique order identifier (our payment UUID or booking ref)
     * @param amount    Payment amount
     * @param currency  e.g. "LKR"
     * @return uppercase MD5 hash string
     */
    public String generateHash(String orderId, BigDecimal amount, String currency) {
        String formattedAmount = formatAmount(amount);
        String secretHash = md5(merchantSecret).toUpperCase();
        String raw = merchantId + orderId + formattedAmount + currency + secretHash;
        return md5(raw).toUpperCase();
    }

    /**
     * Verify the md5sig sent by PayHere in the notify callback.
     * Returns true if the signature is authentic.
     */
    public boolean verifyNotification(
            String orderId,
            String payhereAmount,
            String payhereCurrency,
            String statusCode,
            String md5sig
    ) {
        String secretHash = md5(merchantSecret).toUpperCase();
        String raw = merchantId + orderId + payhereAmount + payhereCurrency + statusCode + secretHash;
        String expected = md5(raw).toUpperCase();
        return expected.equals(md5sig != null ? md5sig.toUpperCase() : "");
    }

    public String getMerchantId() {
        return merchantId;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    /** Format to 2 decimal places as PayHere expects */
    public static String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            Formatter formatter = new Formatter();
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            String result = formatter.toString();
            formatter.close();
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
