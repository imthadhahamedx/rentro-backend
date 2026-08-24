package com.rentro.service;

import com.rentro.dto.request.booking.*;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.booking.*;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    PaginatedResponseDto<BookingListItemResponseDto> findAll(String searchText, String status, int page, int size);

    BookingDetailResponseDto findById(UUID id);

    BookingDetailResponseDto findByRef(String ref);

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
