package com.banking.customer.presentation.mapper;

import com.banking.customer.application.dto.CustomerRequest;
import com.banking.customer.application.dto.CustomerResponse;
import com.banking.customer.application.dto.CustomerUpdateRequest;
import com.banking.customer.presentation.dto.request.CreateCustomerApiRequest;
import com.banking.customer.presentation.dto.request.PatchCustomerApiRequest;
import com.banking.customer.presentation.dto.request.UpdateCustomerApiRequest;
import com.banking.customer.presentation.dto.response.CustomerApiResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerApiMapper {

    public CustomerRequest toApplicationRequest(CreateCustomerApiRequest apiRequest) {
        if (Objects.isNull(apiRequest)) {
            return null;
        }

        CustomerRequest request = new CustomerRequest();
        request.setName(apiRequest.getName());
        request.setLastName(apiRequest.getLastName());
        request.setGender(apiRequest.getGender());
        request.setBirthDate(apiRequest.getBirthDate());
        request.setIdentification(apiRequest.getIdentification());
        request.setAddress(apiRequest.getAddress());
        request.setPhone(apiRequest.getPhone());
        request.setCustomerId(apiRequest.getCustomerId());
        request.setPassword(apiRequest.getPassword());

        return request;
    }

    public CustomerRequest toApplicationUpdateRequest(UpdateCustomerApiRequest apiRequest) {
        if (Objects.isNull(apiRequest)) {
            return null;
        }

        CustomerRequest request = new CustomerRequest();
        request.setName(apiRequest.getName());
        request.setLastName(apiRequest.getLastName());
        request.setGender(apiRequest.getGender());
        request.setBirthDate(apiRequest.getBirthDate());
        request.setAddress(apiRequest.getAddress());
        request.setPhone(apiRequest.getPhone());
        request.setPassword(apiRequest.getPassword());

        return request;
    }

    public CustomerUpdateRequest toApplicationPatchRequest(PatchCustomerApiRequest apiRequest) {
        if (Objects.isNull(apiRequest)) {
            return null;
        }

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setAddress(apiRequest.getAddress());
        request.setPhone(apiRequest.getPhone());
        request.setPassword(apiRequest.getPassword());
        request.setStatus(apiRequest.getStatus());

        return request;
    }

    public CustomerApiResponse toApiResponse(CustomerResponse response) {
        if (Objects.isNull(response)) {
            return null;
        }

        return CustomerApiResponse.builder()
                .id(response.getId())
                .name(response.getName())
                .lastName(response.getLastName())
                .fullName(response.getFullName())
                .gender(response.getGender())
                .birthDate(response.getBirthDate())
                .age(response.getAge())
                .identification(response.getIdentification())
                .address(response.getAddress())
                .phone(response.getPhone())
                .customerId(response.getCustomerId())
                .status(response.getStatus())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

}