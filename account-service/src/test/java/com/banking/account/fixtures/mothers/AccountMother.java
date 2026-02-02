package com.banking.account.fixtures.mothers;

import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountType;
import com.banking.account.fixtures.builders.AccountBuilder;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountMother {

    public static Account defaultAccount() {
        return AccountBuilder.anAccount().build();
    }

    public static Account activeAccount() {
        return AccountBuilder.anAccount()
                .active()
                .build();
    }

    public static Account inactiveAccount() {
        return AccountBuilder.anAccount()
                .inactive()
                .build();
    }

    public static Account suspendedAccount() {
        return AccountBuilder.anAccount()
                .suspended()
                .build();
    }

    public static Account closedAccount() {
        return AccountBuilder.anAccount()
                .closed()
                .build();
    }

    public static Account savingsAccount() {
        return AccountBuilder.anAccount()
                .savings()
                .build();
    }

    public static Account checkingAccount() {
        return AccountBuilder.anAccount()
                .checking()
                .build();
    }

    public static Account accountWithZeroBalance() {
        return AccountBuilder.anAccount()
                .withZeroBalance()
                .build();
    }

    public static Account accountWithNegativeBalance() {
        return AccountBuilder.anAccount()
                .withNegativeBalance()
                .build();
    }

    public static Account accountWithSpecificCustomer(UUID customerId) {
        return AccountBuilder.anAccount()
                .withCustomerId(customerId)
                .build();
    }

    public static Account newlyCreatedAccount() {
        return Account.create(
                "1234567890",
                AccountType.SAVINGS,
                new BigDecimal("1000.00"),
                UUID.randomUUID()
        );
    }

    public static Account accountWithLowBalance() {
        return AccountBuilder.anAccount()
                .withCurrentBalance(new BigDecimal("50.00"))
                .build();
    }

    public static Account accountWithHighBalance() {
        return AccountBuilder.anAccount()
                .withCurrentBalance(new BigDecimal("10000.00"))
                .build();
    }

}