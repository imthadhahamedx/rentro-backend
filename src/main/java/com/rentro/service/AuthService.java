package com.rentro.service;

import com.rentro.dto.request.LoginRequestDto;
import com.rentro.dto.request.RegisterRequestDto;
import com.rentro.dto.response.AuthResponseDto;

public interface AuthService {

    void register(RegisterRequestDto dto);

    AuthResponseDto login(LoginRequestDto dto);
}
