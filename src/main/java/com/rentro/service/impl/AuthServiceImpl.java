package com.rentro.service.impl;

import com.rentro.dto.request.LoginRequestDto;
import com.rentro.dto.request.RegisterRequestDto;
import com.rentro.dto.response.AuthResponseDto;
import com.rentro.entity.UserEntity;
import com.rentro.exception.DuplicateEntryException;
import com.rentro.repository.UserRepository;
import com.rentro.service.AuthService;
import com.rentro.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterRequestDto dto){
        if(repository.existsByEmail(dto.getEmail())){
            throw new DuplicateEntryException("Email already used");
        }

        UserEntity user = UserEntity.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(UserEntity.Role.CUSTOMER)
                .phoneNumber(dto.getPhoneNumber())
                .isActive(true)
                .build();

        repository.save(user);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(), dto.getPassword()
                )
        );
        UserEntity userEntity = repository.findUserEntityByEmail(dto.getEmail())
                .orElseThrow(()-> new RuntimeException("User not found"));

        String token = jwtUtil.generateAccessToken(userEntity);
        return AuthResponseDto.builder()
                .role(userEntity.getRole().name())
                .token(token)
                .tokenType("Bearer")
                .fullName(userEntity.getFullName())
                .email(userEntity.getEmail())
                .build();
    }
}
