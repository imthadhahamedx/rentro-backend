package com.rentro.config;

import com.rentro.entity.UserEntity;
import com.rentro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Seed accounts ────────────────────────────────────────────────────────
    // Change these credentials before deploying to production.

    private static final List<SeedUser> SEED_USERS = List.of(

            new SeedUser(
                    "Super Admin",
                    "superadmin@rentro.com",
                    "SuperAdmin@123",
                    "0771000001",
                    UserEntity.Role.SUPER_ADMIN
            ),

            new SeedUser(
                    "Admin User",
                    "admin@rentro.com",
                    "Admin@1234",
                    "0771000002",
                    UserEntity.Role.ADMIN
            ),

            new SeedUser(
                    "Staff Member",
                    "staff@rentro.com",
                    "Staff@1234",
                    "0771000003",
                    UserEntity.Role.STAFF
            )
    );

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void run(String... args) {
        log.info("DataSeeder: checking seed accounts");

        for (SeedUser seed : SEED_USERS) {
            if (userRepository.existsByEmail(seed.email())) {
                log.info("[SKIP]   {} — already exists ({})", seed.role(), seed.email());
                continue;
            }

            UserEntity userEntity = UserEntity.builder()
                    .fullName(seed.fullName())
                    .email(seed.email())
                    .passwordHash(passwordEncoder.encode(seed.rawPassword()))
                    .phoneNumber(seed.phoneNumber())
                    .role(seed.role())
                    .isActive(true)
                    .emailVerified(true)
                    .build();

            userRepository.save(userEntity);
            log.info("[SAVED]  {} — created ({})", seed.role(), seed.email());
        }

        log.info("DataSeeder: done");
    }

    // ─── Simple record to hold seed data ─────────────────────────────────────

    private record SeedUser(
            String fullName,
            String email,
            String rawPassword,
            String phoneNumber,
            UserEntity.Role role
    ) {}
}
