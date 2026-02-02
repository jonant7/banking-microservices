package com.banking.account.application.port.in;

import com.banking.account.application.dto.AccountRequest;
import com.banking.account.application.dto.AccountResponse;

public interface CreateAccountUseCase {

    AccountResponse createAccount(AccountRequest request);

}