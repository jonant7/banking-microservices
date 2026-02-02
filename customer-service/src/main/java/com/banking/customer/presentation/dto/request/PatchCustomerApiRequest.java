package com.banking.customer.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchCustomerApiRequest {

    @Size(max = 500, message = "{validation.customer.address.size}")
    private String address;

    @Pattern(regexp = "^[0-9]{7,20}$", message = "{validation.customer.phone.pattern}")
    private String phone;

    @Size(min = 4, max = 100, message = "{validation.customer.password.size}")
    private String password;

    private Boolean status;

}