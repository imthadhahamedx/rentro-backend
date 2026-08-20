package com.rentro.dto.request.damage;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DamageFixedUpdateRequestDto {

    @NotNull(message = "isFixed is required")
    private Boolean isFixed;

    @Size(max = 2000, message = "Remark cannot exceed 2000 characters")
    private String remark;
}
