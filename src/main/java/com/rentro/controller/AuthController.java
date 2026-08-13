package com.rentro.controller;

import com.rentro.dto.request.LoginRequestDto;
import com.rentro.dto.request.RegisterRequestDto;
import com.rentro.dto.response.AuthResponseDto;
import com.rentro.dto.response.StandardResponseDto;
import com.rentro.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<StandardResponseDto> register(@Valid @RequestBody RegisterRequestDto dto) {
        service.register(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDto.builder()
                        .code(201)
                        .message("User registered successfully")
                        .data(null)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        AuthResponseDto authResponseDto = service.login(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StandardResponseDto.builder()
                        .code(201)
                        .message("User logged successfully")
                        .data(authResponseDto)
                        .build());
    }
}
