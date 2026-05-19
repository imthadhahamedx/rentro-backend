package com.rentro.service.impl;

import com.rentro.dto.SecurityUser;
import com.rentro.entity.UserEntity;
import com.rentro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = repository.findUserEntityByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));

        return new SecurityUser(userEntity);
    }
}
