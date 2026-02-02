package com.banking.account.presentation.dto.request;

import com.banking.account.domain.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionApiRequest {

    @NotNull(message = "{validation.transaction.type.notNull}")
    private TransactionType type;

    @NotNull(message = "{validation.transaction.amount.notNull}")
    @DecimalMin(value = "0.01", inclusive = true, message = "{validation.transaction.amount.min}")
    @Digits(integer = 15, fraction = 2, message = "{validation.transaction.amount.digits}")
    private BigDecimal amount;

    @Size(max = 255, message = "{validation.transaction.reference.size}")
    private String reference;

}