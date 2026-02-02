package com.banking.account.application.dto;

import com.banking.account.domain.model.TransactionType;
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
public class TransactionRequest {

    private UUID accountId;
    private TransactionType type;
    private BigDecimal amount;
    private String reference;

}