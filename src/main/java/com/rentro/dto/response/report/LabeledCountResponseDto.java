package com.rentro.dto.response.report;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LabeledCountResponseDto {
    private String label;
    private long count;
}
