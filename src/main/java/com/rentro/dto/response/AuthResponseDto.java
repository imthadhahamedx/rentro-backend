package com.rentro.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AuthResponseDto {

    private String token;
    private String tokenType;
    private String email;
    private String fullName;
    private String role;
}
