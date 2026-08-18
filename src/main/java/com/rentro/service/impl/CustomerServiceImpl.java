package com.rentro.service.impl;

import com.rentro.dto.request.customer.CustomerAddressRequestDto;
import com.rentro.dto.request.customer.CustomerCreateRequestDto;
import com.rentro.dto.request.customer.CustomerStatusUpdateRequestDto;
import com.rentro.dto.request.customer.CustomerUpdateRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.booking.BookingListItemResponseDto;
import com.rentro.dto.response.customer.CustomerListItemResponseDto;
import com.rentro.dto.response.customer.CustomerResponseDto;
import com.rentro.entity.AddressEntity;
import com.rentro.entity.BookingEntity;
import com.rentro.entity.CustomerEntity;
import com.rentro.entity.UserEntity;
import com.rentro.exception.DuplicateEntryException;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.AddressRepository;
import com.rentro.repository.BookingRepository;
import com.rentro.repository.CustomerRepository;
import com.rentro.repository.UserRepository;
import com.rentro.service.CustomerService;
import com.rentro.util.mapper.BookingMapper;
import com.rentro.util.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto search(String searchText, Boolean isActive, int page, int size) {
        Page<CustomerEntity> pageResult = customerRepository.search(
                searchText == null ? "" : searchText, isActive, PageRequest.of(page, size)
        );

        return PaginatedResponseDto.<CustomerListItemResponseDto>builder()
                .count(pageResult.getTotalElements())
                .dataList(pageResult.getContent().stream().map(customerMapper::toCustomerListItemResponseDto).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto findById(UUID id) {
        CustomerEntity customer = getCustomerWithDetailsOrThrow(id);
        long totalBookings = bookingRepository.countByCustomerId(id);
        return customerMapper.toCustomerResponseDto(customer, totalBookings);
    }

    @Override
    public CustomerResponseDto create(CustomerCreateRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEntryException("A user with email '" + dto.getEmail() + "' already exists");
        }
        if (customerRepository.existsByNicIgnoreCase(dto.getNic())) {
            throw new DuplicateEntryException("A customer with NIC '" + dto.getNic() + "' already exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .role(UserEntity.Role.CUSTOMER)
                .isActive(true)
                .emailVerified(false)
                .build();
        userEntity = userRepository.save(userEntity);

        CustomerEntity customer = CustomerEntity.builder()
                .nic(dto.getNic())
                .drivingLicenseNo(dto.getDrivingLicenseNo())
                .licenseExpiryDate(dto.getLicenseExpiryDate())
                .dateOfBirth(dto.getDateOfBirth())
                .notes(dto.getNotes())
                .user(userEntity)
                .build();
        customer = customerRepository.save(customer);

        if (hasContent(dto.getAddress())) {
            addressRepository.save(toNewAddress(dto.getAddress(), customer));
        }

        return findById(customer.getId());
    }

    @Override
    public CustomerResponseDto update(UUID id, CustomerUpdateRequestDto dto) {
        CustomerEntity customer = getCustomerWithDetailsOrThrow(id);
        UserEntity userEntity = customer.getUser();

        if (!userEntity.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEntryException("A user with email '" + dto.getEmail() + "' already exists");
        }
        if (customerRepository.existsByNicIgnoreCaseAndIdNot(dto.getNic(), id)) {
            throw new DuplicateEntryException("A customer with NIC '" + dto.getNic() + "' already exists");
        }

        userEntity.setFullName(dto.getFullName());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPhoneNumber(dto.getPhoneNumber());
        if (StringUtils.hasText(dto.getPassword())) {
            userEntity.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        userRepository.save(userEntity);

        customer.setNic(dto.getNic());
        customer.setDrivingLicenseNo(dto.getDrivingLicenseNo());
        customer.setLicenseExpiryDate(dto.getLicenseExpiryDate());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setNotes(dto.getNotes());
        customerRepository.save(customer);

        if (hasContent(dto.getAddress())) {
            if (customer.getAddresses() != null && !customer.getAddresses().isEmpty()) {
                AddressEntity existing = customer.getAddresses().get(0);
                applyAddress(existing, dto.getAddress());
                addressRepository.save(existing);
            } else {
                addressRepository.save(toNewAddress(dto.getAddress(), customer));
            }
        }

        return findById(id);
    }

    @Override
    public void updateStatus(UUID id, CustomerStatusUpdateRequestDto dto) {
        CustomerEntity customerEntity = getCustomerWithDetailsOrThrow(id);
        UserEntity userEntity = customerEntity.getUser();
        userEntity.setActive(dto.getIsActive());
        userRepository.save(userEntity);
    }

    @Override
    public void deleteById(UUID id) {
        CustomerEntity customerEntity = getCustomerWithDetailsOrThrow(id);

        long bookingCount = bookingRepository.countByCustomerId(id);
        if (bookingCount > 0) {
            throw new ValidationException(
                    "Cannot delete a customer with existing bookings (" + bookingCount +
                            "). Deactivate the account instead."
            );
        }

        UserEntity userEntity = customerEntity.getUser();
        customerRepository.delete(customerEntity);
        if (userEntity != null) {
            userRepository.delete(userEntity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto findBookings(UUID customerId, String status, int page, int size) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntryNotFoundException("Customer not found");
        }

        BookingEntity.BookingStatus statusEnum = parseStatusOrNull(status);
        Page<BookingEntity> pageResult = bookingRepository.searchByCustomer(customerId, statusEnum, PageRequest.of(page, size));

        return PaginatedResponseDto.<BookingListItemResponseDto>builder()
                .count(pageResult.getTotalElements())
                .dataList(pageResult.getContent().stream().map(bookingMapper::toBookingListItemResponseDto).toList())
                .build();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────
    private CustomerEntity getCustomerWithDetailsOrThrow(UUID id) {
        return customerRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntryNotFoundException("Customer not found"));
    }

    private boolean hasContent(CustomerAddressRequestDto dto) {
        return dto != null && (
                StringUtils.hasText(dto.getAddress()) ||
                        StringUtils.hasText(dto.getCity()) ||
                        StringUtils.hasText(dto.getCountry())
        );
    }

    private AddressEntity toNewAddress(CustomerAddressRequestDto dto, CustomerEntity customerEntity) {
        return AddressEntity.builder()
                .address(dto.getAddress())
                .city(dto.getCity())
                .country(dto.getCountry())
                .customer(customerEntity)
                .build();
    }

    private void applyAddress(AddressEntity addressEntity, CustomerAddressRequestDto dto) {
        addressEntity.setAddress(dto.getAddress());
        addressEntity.setCity(dto.getCity());
        addressEntity.setCountry(dto.getCountry());
    }

    private BookingEntity.BookingStatus parseStatusOrNull(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return BookingEntity.BookingStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status: " + status);
        }
    }
}
