package com.rentro.dto.request.customer;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerStatusUpdateRequestDto {

    @NotNull(message = "isActive is required")
    private Boolean isActive;
}
