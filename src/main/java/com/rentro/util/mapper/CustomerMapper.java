package com.rentro.util.mapper;

import com.rentro.dto.response.customer.CustomerAddressResponseDto;
import com.rentro.dto.response.customer.CustomerListItemResponseDto;
import com.rentro.dto.response.customer.CustomerResponseDto;
import com.rentro.entity.AddressEntity;
import com.rentro.entity.CustomerEntity;
import com.rentro.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {

    public CustomerListItemResponseDto toCustomerListItemResponseDto(CustomerEntity customerEntity) {
        if (customerEntity == null) return null;
        UserEntity userEntity = customerEntity.getUser();
        return CustomerListItemResponseDto.builder()
                .id(customerEntity.getId())
                .fullName(userEntity != null ? userEntity.getFullName() : null)
                .email(userEntity != null ? userEntity.getEmail() : null)
                .phoneNumber(userEntity != null ? userEntity.getPhoneNumber() : null)
                .nic(customerEntity.getNic())
                .drivingLicenseNo(customerEntity.getDrivingLicenseNo())
                .licenseExpiryDate(customerEntity.getLicenseExpiryDate())
                .isActive(userEntity != null && userEntity.isActive())
                .createdAt(userEntity != null ? userEntity.getCreatedAt() : null)
                .build();
    }

    public CustomerResponseDto toCustomerResponseDto(CustomerEntity customerEntity, long totalBookings) {
        if (customerEntity == null) return null;
        UserEntity userEntity = customerEntity.getUser();
        return CustomerResponseDto.builder()
                .id(customerEntity.getId())
                .fullName(userEntity != null ? userEntity.getFullName() : null)
                .email(userEntity != null ? userEntity.getEmail() : null)
                .phoneNumber(userEntity != null ? userEntity.getPhoneNumber() : null)
                .nic(customerEntity.getNic())
                .drivingLicenseNo(customerEntity.getDrivingLicenseNo())
                .licenseExpiryDate(customerEntity.getLicenseExpiryDate())
                .dateOfBirth(customerEntity.getDateOfBirth())
                .notes(customerEntity.getNotes())
                .isActive(userEntity != null && userEntity.isActive())
                .emailVerified(userEntity != null && userEntity.isEmailVerified())
                .createdAt(userEntity != null ? userEntity.getCreatedAt() : null)
                .updatedAt(userEntity != null ? userEntity.getUpdatedAt() : null)
                .addresses(
                        customerEntity.getAddresses() == null ? List.of() :
                                customerEntity.getAddresses().stream().map(this::toCustomerAddressResponseDto).toList()
                )
                .totalBookings(totalBookings)
                .build();
    }

    public CustomerAddressResponseDto toCustomerAddressResponseDto(AddressEntity addressEntity) {
        if (addressEntity == null) return null;
        return CustomerAddressResponseDto.builder()
                .id(addressEntity.getId())
                .address(addressEntity.getAddress())
                .city(addressEntity.getCity())
                .country(addressEntity.getCountry())
                .build();
    }
}
