package com.banking.account.application.port.in;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.application.dto.AccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetAccountUseCase {

    AccountResponse getAccountById(UUID id);

    AccountResponse getAccountByAccountNumber(String accountNumber);

    Page<AccountResponse> getAllAccounts(AccountFilter filter, Pageable pageable);

}