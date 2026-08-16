package com.rentro.service;

import com.rentro.dto.request.booking.*;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.booking.BookingDetailResponseDto;
import com.rentro.dto.response.booking.CustomerOptionResponseDto;
import com.rentro.dto.response.booking.LocationOptionResponseDto;
import com.rentro.dto.response.booking.VehicleOptionResponseDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    PaginatedResponseDto findAll(String searchText, String status, int page, int size);

    BookingDetailResponseDto findById(UUID id);

    UUID create(BookingCreateRequestDto dto);

    void confirm(UUID id);

    void activate(UUID id);

    void complete(UUID id, BookingCompleteRequestDto dto);

    void cancel(UUID id, BookingCancelRequestDto dto);

    void extend(UUID id, BookingExtensionRequestDto dto);

    void updateNotes(UUID id, BookingNotesRequestDto dto);

    List<CustomerOptionResponseDto> findCustomerOptions(String searchText);

    List<VehicleOptionResponseDto> findAvailableVehicleOptions(String searchText);

    List<LocationOptionResponseDto> findLocationOptions();
}
