package com.banking.customer.application.dto;

import com.banking.customer.domain.model.CustomerStatus;
import com.banking.customer.domain.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter {

    private String name;
    private String lastName;
    private String identification;
    private String customerId;
    private Gender gender;
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;
    private Integer minAge;
    private Integer maxAge;
    private CustomerStatus status;
    private String address;
    private String phone;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

}