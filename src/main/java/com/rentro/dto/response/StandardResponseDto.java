package com.rentro.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class StandardResponseDto {

    private Integer code;
    private String message;
    private Object data;
}
