package com.banking.account.application.port.in;

import com.banking.account.application.dto.TransactionRequest;
import com.banking.account.application.dto.TransactionResponse;
import com.banking.account.domain.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionUseCase {

    TransactionResponse executeTransaction(TransactionRequest request);

    TransactionResponse getTransactionById(UUID id);

    Page<TransactionResponse> getTransactionsByAccountId(UUID accountId, Pageable pageable);

    List<TransactionResponse> getTransactionsByDateRange(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);

    List<TransactionResponse> getTransactionsByAccountIdAndType(UUID accountId, TransactionType type, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalAmountByType(UUID accountId, TransactionType type, LocalDateTime startDate, LocalDateTime endDate);

}