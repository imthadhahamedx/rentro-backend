package com.rentro.dto.request.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingCancelRequestDto {

    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;
}
