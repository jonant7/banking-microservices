package com.banking.account.application.mapper;

import com.banking.account.application.dto.AccountResponse;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AccountResponseMapper {

    private final CustomerEventListener customerEventListener;

    public AccountResponse toResponse(Account account) {
        if (Objects.isNull(account)) {
            return null;
        }

        String customerName = null;
        if (Objects.nonNull(account.getCustomerId())) {
            String customerInfo = customerEventListener.getCustomerName(account.getCustomerId());
            if (Objects.nonNull(customerInfo)) {
                customerName = customerInfo;
            }
        }

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumberValue())
                .accountType(account.getAccountType())
                .initialBalance(account.getInitialBalance().value())
                .currentBalance(account.getCurrentBalance().value())
                .status(account.getStatus())
                .customerId(account.getCustomerId())
                .customerName(customerName)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public TransactionResponse toResponse(Transaction transaction) {
        if (Objects.isNull(transaction)) {
            return null;
        }

        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount().value())
                .balanceBefore(transaction.getBalanceBefore().value())
                .balanceAfter(transaction.getBalanceAfter().value())
                .reference(transaction.getReference())
                .accountId(transaction.getAccountId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

}