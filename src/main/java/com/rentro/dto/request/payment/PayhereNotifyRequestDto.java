package com.rentro.dto.request.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps the x-www-form-urlencoded body that PayHere POSTs to /payments/payhere/notify.
 * Field names must match PayHere documentation exactly.
 */
@Getter
@Setter
@NoArgsConstructor
public class PayhereNotifyRequestDto {

    private String merchant_id;
    private String order_id;
    private String payment_id;
    private String payhere_amount;
    private String payhere_currency;
    private String status_code;    // 2 = SUCCESS, 0 = PENDING, -1 = CANCELED, -2 = FAILED, -3 = CHARGEBACK
    private String md5sig;
    private String method;
    private String status_message;
    private String card_holder_name;
    private String card_no;
    private String card_expiry;
    private String recurring;
    private String message_type;
    private String item_recurrence;
    private String item_duration;
    private String item_rec_status;
    private String item_rec_date_after;
    private String item_rec_install_paid;
    private String custom_1;   // we store our internal payment UUID here
    private String custom_2;
}
