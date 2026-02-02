package com.banking.account.application.port.in;

import com.banking.account.application.dto.AccountStatementReport;

import java.time.LocalDateTime;
import java.util.UUID;

public interface GenerateAccountStatementUseCase {

    AccountStatementReport generateStatement(
            UUID customerId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

}