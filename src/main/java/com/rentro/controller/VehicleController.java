package com.rentro.controller;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("public/search")
    public PaginatedResponseDto search(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return vehicleService.search(searchText, page, size);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public PaginatedResponseDto findAllForAdmin(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return vehicleService.findAllForAdmin(searchText, page, size);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public void create( @Valid @RequestBody VehicleRequestDto dto) {
        vehicleService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void update( @PathVariable UUID id, @Valid @RequestBody VehicleRequestDto dto) {
        vehicleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public void deleteById( @PathVariable UUID id) {
        vehicleService.deleteById(id);
    }
}
