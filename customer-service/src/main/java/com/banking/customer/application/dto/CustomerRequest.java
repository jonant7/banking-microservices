package com.banking.customer.application.dto;

import com.banking.customer.domain.model.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "{validation.customer.name.notBlank}")
    @Size(min = 2, max = 100, message = "{validation.customer.name.size}")
    private String name;

    @NotBlank(message = "{validation.customer.lastName.notBlank}")
    @Size(min = 2, max = 100, message = "{validation.customer.lastName.size}")
    private String lastName;

    @NotNull(message = "{validation.customer.gender.notNull}")
    private Gender gender;

    @NotNull(message = "{validation.customer.birthDate.notNull}")
    @Past(message = "{validation.customer.birthDate.past}")
    private LocalDate birthDate;

    @NotBlank(message = "{validation.customer.identification.notBlank}")
    @Size(min = 10, max = 20, message = "{validation.customer.identification.size}")
    private String identification;

    @NotBlank(message = "{validation.customer.address.notBlank}")
    @Size(max = 500, message = "{validation.customer.address.size}")
    private String address;

    @Pattern(regexp = "^[0-9]{7,20}$", message = "{validation.customer.phone.pattern}")
    private String phone;

    @NotBlank(message = "{validation.customer.customerId.notBlank}")
    @Size(min = 3, max = 50, message = "{validation.customer.customerId.size}")
    private String customerId;

    @NotBlank(message = "{validation.customer.password.notBlank}")
    @Size(min = 4, max = 100, message = "{validation.customer.password.size}")
    private String password;

}