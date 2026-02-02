package com.banking.customer.application.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateRequest {

    @Size(max = 500, message = "{validation.customer.address.size}")
    private String address;

    @Pattern(regexp = "^[0-9]{7,20}$", message = "{validation.customer.phone.pattern}")
    private String phone;

    @Size(min = 4, max = 100, message = "{validation.customer.password.size}")
    private String password;

    private Boolean status;

}