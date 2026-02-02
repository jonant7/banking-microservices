package com.banking.customer.application.dto;

import com.banking.customer.domain.model.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerResponse {

    private UUID id;
    private String name;
    private String lastName;
    private String fullName;
    private Gender gender;
    private LocalDate birthDate;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
    private String customerId;
    private Boolean status;
    private Instant createdAt;
    private Instant updatedAt;

}