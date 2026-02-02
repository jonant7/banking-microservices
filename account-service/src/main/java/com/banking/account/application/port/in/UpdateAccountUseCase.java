package com.banking.account.application.port.in;

import com.banking.account.application.dto.AccountResponse;

import java.util.UUID;

public interface UpdateAccountUseCase {

    AccountResponse activateAccount(UUID id);

    AccountResponse deactivateAccount(UUID id);

}