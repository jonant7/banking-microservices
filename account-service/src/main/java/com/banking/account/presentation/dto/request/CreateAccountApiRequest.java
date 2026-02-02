package com.banking.account.presentation.dto.request;

import com.banking.account.domain.model.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountApiRequest {

    @NotBlank(message = "{validation.account.accountNumber.notBlank}")
    @Pattern(regexp = "^[0-9]{6,20}$", message = "{validation.account.accountNumber.pattern}")
    private String accountNumber;

    @NotNull(message = "{validation.account.accountType.notNull}")
    private AccountType accountType;

    @NotNull(message = "{validation.account.initialBalance.notNull}")
    @DecimalMin(value = "0.0", inclusive = true, message = "{validation.account.initialBalance.min}")
    @Digits(integer = 15, fraction = 2, message = "{validation.account.initialBalance.digits}")
    private BigDecimal initialBalance;

    @NotNull(message = "{validation.account.customerId.notNull}")
    private UUID customerId;

}