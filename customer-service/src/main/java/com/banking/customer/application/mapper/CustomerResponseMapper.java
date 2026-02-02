package com.banking.customer.application.mapper;

import com.banking.customer.application.dto.CustomerResponse;
import com.banking.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerResponseMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (Objects.isNull(customer)) {
            return null;
        }

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .lastName(customer.getLastName())
                .fullName(customer.getFullName())
                .gender(customer.getGender())
                .birthDate(customer.getBirthDate())
                .age(customer.getAge())
                .identification(customer.getIdentificationValue())
                .address(customer.getAddress())
                .phone(customer.getPhoneValue())
                .customerId(customer.getCustomerIdValue())
                .status(customer.isActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

}