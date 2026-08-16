package com.rentro.dto.request.booking;

import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingNotesRequestDto {

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;
}
