package com.banking.account.presentation.dto.response;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountApiResponse {

    private UUID id;

    private String accountNumber;

    private AccountType accountType;

    private BigDecimal initialBalance;

    private BigDecimal currentBalance;

    private AccountStatus status;

    private UUID customerId;

    private String customerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Instant updatedAt;

}