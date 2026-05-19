package com.rentro.dto.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PaginatedResponseDto<T> {
    private long count;
    private List<T> dataList;
}
