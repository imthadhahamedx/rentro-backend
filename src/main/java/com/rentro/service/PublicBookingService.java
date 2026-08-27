package com.rentro.service;

import com.rentro.dto.request.PublicBookingRequestDto;
import com.rentro.dto.response.PublicBookingResponseDto;

public interface PublicBookingService {
    PublicBookingResponseDto createRequest(PublicBookingRequestDto dto);
}
