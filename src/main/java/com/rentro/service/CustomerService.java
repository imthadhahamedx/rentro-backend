package com.rentro.service;

import com.rentro.dto.request.customer.CustomerCreateRequestDto;
import com.rentro.dto.request.customer.CustomerStatusUpdateRequestDto;
import com.rentro.dto.request.customer.CustomerUpdateRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.customer.CustomerResponseDto;

import java.util.UUID;

public interface CustomerService {

    PaginatedResponseDto search(String searchText, Boolean isActive, int page, int size);

    CustomerResponseDto findById(UUID id);

    CustomerResponseDto create(CustomerCreateRequestDto dto);

    CustomerResponseDto update(UUID id, CustomerUpdateRequestDto dto);

    void updateStatus(UUID id, CustomerStatusUpdateRequestDto dto);

    void deleteById(UUID id);

    PaginatedResponseDto findBookings(UUID customerId, String status, int page, int size);
}
