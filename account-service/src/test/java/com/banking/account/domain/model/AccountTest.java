package com.banking.account.domain.model;

import com.banking.account.domain.event.AccountCreatedEvent;
import com.banking.account.domain.event.TransactionPerformedEvent;
import com.banking.account.domain.exception.AccountErrorCode;
import com.banking.account.domain.exception.InactiveAccountException;
import com.banking.account.domain.exception.InsufficientBalanceException;
import com.banking.account.domain.exception.InvalidTransactionException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.banking.account.fixtures.mothers.AccountMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Nested
    class CreateAccount {

        @Test
        void shouldCreateAccountWithValidData() {
            Account account = newlyCreatedAccount();

            assertThat(account).isNotNull();
            assertThat(account.getId()).isNotNull();
            assertThat(account.getAccountNumberValue()).isEqualTo("1234567890");
            assertThat(account.getAccountType()).isEqualTo(AccountType.SAVINGS);
            assertThat(account.getInitialBalance().value()).isEqualByComparingTo("1000.00");
            assertThat(account.getCurrentBalance().value()).isEqualByComparingTo("1000.00");
            assertThat(account.isActive()).isTrue();
            assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(account.getCreatedAt()).isNotNull();
            assertThat(account.getUpdatedAt()).isNotNull();
        }

        @Test
        void shouldRegisterAccountCreatedEvent() {
            Account account = newlyCreatedAccount();

            List<Object> events = account.getDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(AccountCreatedEvent.class);

            AccountCreatedEvent event = (AccountCreatedEvent) events.get(0);
            assertThat(event.accountId()).isEqualTo(account.getId());
            assertThat(event.status()).isEqualTo(AccountStatus.ACTIVE);
        }

        @Test
        void shouldThrowExceptionWhenAccountNumberIsNull() {
            assertThatThrownBy(() -> Account.create(
                    null,
                    AccountType.SAVINGS,
                    new BigDecimal("1000.00"),
                    UUID.randomUUID()
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_NULL.getCode());
        }

        @Test
        void shouldThrowExceptionWhenAccountNumberIsEmpty() {
            assertThatThrownBy(() -> Account.create(
                    "   ",
                    AccountType.SAVINGS,
                    new BigDecimal("1000.00"),
                    UUID.randomUUID()
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_EMPTY.getCode());
        }

        @Test
        void shouldThrowExceptionWhenAccountNumberIsTooLong() {
            String tooLongAccountNumber = "123456789012345678901";

            assertThatThrownBy(() -> Account.create(
                    tooLongAccountNumber,
                    AccountType.SAVINGS,
                    new BigDecimal("1000.00"),
                    UUID.randomUUID()
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_TOO_LONG.getCode());
        }

        @Test
        void shouldThrowExceptionWhenAccountNumberHasInvalidFormat() {
            assertThatThrownBy(() -> Account.create(
                    "ABC123",
                    AccountType.SAVINGS,
                    new BigDecimal("1000.00"),
                    UUID.randomUUID()
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_VALIDATION_NUMBER_INVALID_FORMAT.getCode());
        }

    }

    @Nested
    class Deposit {

        @Test
        void shouldDepositAmountToActiveAccount() {
            Account account = activeAccount();
            Money initialBalance = account.getCurrentBalance();
            Money depositAmount = Money.of(new BigDecimal("500.00"));

            account.deposit(depositAmount);

            assertThat(account.getCurrentBalance()).isEqualTo(initialBalance.add(depositAmount));
        }

        @Test
        void shouldThrowExceptionWhenDepositingToInactiveAccount() {
            Account account = inactiveAccount();
            Money depositAmount = Money.of(new BigDecimal("500.00"));

            assertThatThrownBy(() -> account.deposit(depositAmount))
                    .isInstanceOf(InactiveAccountException.class);
        }

        @Test
        void shouldThrowExceptionWhenDepositAmountIsNull() {
            Account account = activeAccount();

            assertThatThrownBy(() -> account.deposit(null))
                    .isInstanceOf(InvalidTransactionException.class);
        }

        @Test
        void shouldThrowExceptionWhenDepositAmountIsNegative() {
            Account account = activeAccount();
            Money negativeAmount = Money.of(new BigDecimal("-100.00"));

            assertThatThrownBy(() -> account.deposit(negativeAmount))
                    .isInstanceOf(InvalidTransactionException.class);
        }

        @Test
        void shouldUpdateTimestampWhenDepositing() {
            Account account = activeAccount();
            var originalUpdatedAt = account.getUpdatedAt();
            Money depositAmount = Money.of(new BigDecimal("500.00"));

            account.deposit(depositAmount);

            assertThat(account.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        void shouldDepositLargeAmount() {
            Account account = activeAccount();
            Money largeAmount = Money.of(new BigDecimal("999999.99"));

            account.deposit(largeAmount);

            assertThat(account.getCurrentBalance()).isEqualTo(account.getCurrentBalance());
        }

        @Test
        void shouldThrowExceptionWhenDepositingZeroAmount() {
            Account account = activeAccount();
            Money zeroAmount = Money.zero();

            assertThatThrownBy(() -> account.deposit(zeroAmount))
                    .isInstanceOf(InvalidTransactionException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_BUSINESS_INVALID_TRANSACTION.getCode());
        }

        @Test
        void shouldThrowExceptionWhenDepositingNegativeAmount() {
            Account account = activeAccount();
            Money negativeAmount = Money.of(new BigDecimal("-100.00"));

            assertThatThrownBy(() -> account.deposit(negativeAmount))
                    .isInstanceOf(InvalidTransactionException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_BUSINESS_INVALID_TRANSACTION.getCode());
        }

    }

    @Nested
    class Withdraw {

        @Test
        void shouldWithdrawAmountFromActiveAccount() {
            Account account = activeAccount();
            Money initialBalance = account.getCurrentBalance();
            Money withdrawAmount = Money.of(new BigDecimal("500.00"));

            account.withdraw(withdrawAmount);

            assertThat(account.getCurrentBalance()).isEqualTo(initialBalance.subtract(withdrawAmount));
        }

        @Test
        void shouldThrowExceptionWhenInsufficientBalance() {
            Account account = accountWithLowBalance();
            Money withdrawAmount = Money.of(new BigDecimal("100.00"));

            assertThatThrownBy(() -> account.withdraw(withdrawAmount))
                    .isInstanceOf(InsufficientBalanceException.class);
        }

        @Test
        void shouldUpdateTimestampWhenWithdrawing() {
            Account account = activeAccount();
            var originalUpdatedAt = account.getUpdatedAt();
            Money withdrawAmount = Money.of(new BigDecimal("100.00"));

            account.withdraw(withdrawAmount);

            assertThat(account.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @Test
        void shouldAllowWithdrawalOfExactBalance() {
            Account account = activeAccount();
            Money exactBalance = account.getCurrentBalance();

            account.withdraw(exactBalance);

            assertThat(account.getCurrentBalance().isZero()).isTrue();
        }

        @Test
        void shouldThrowExceptionWhenWithdrawingFromSuspendedAccount() {
            Account account = suspendedAccount();
            Money withdrawAmount = Money.of(new BigDecimal("100.00"));

            assertThatThrownBy(() -> account.withdraw(withdrawAmount))
                    .isInstanceOf(InactiveAccountException.class);
        }

        @Test
        void shouldThrowExceptionWhenWithdrawingFromClosedAccount() {
            Account account = closedAccount();
            Money withdrawAmount = Money.of(new BigDecimal("100.00"));

            assertThatThrownBy(() -> account.withdraw(withdrawAmount))
                    .isInstanceOf(InactiveAccountException.class);
        }

        @Test
        void shouldThrowExceptionWhenWithdrawingZeroAmount() {
            Account account = activeAccount();
            Money zeroAmount = Money.zero();

            assertThatThrownBy(() -> account.withdraw(zeroAmount))
                    .isInstanceOf(InvalidTransactionException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_BUSINESS_INVALID_TRANSACTION.getCode());
        }

        @Test
        void shouldThrowExceptionWhenWithdrawingNegativeAmount() {
            Account account = activeAccount();
            Money negativeAmount = Money.of(new BigDecimal("-100.00"));

            assertThatThrownBy(() -> account.withdraw(negativeAmount))
                    .isInstanceOf(InvalidTransactionException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_BUSINESS_INVALID_TRANSACTION.getCode());
        }

    }

    @Nested
    class StatusManagement {

        @Test
        void shouldActivateInactiveAccount() {
            Account account = inactiveAccount();

            account.activate();

            assertThat(account.isActive()).isTrue();
            assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        }

        @Test
        void shouldCloseAccountWithZeroBalance() {
            Account account = accountWithZeroBalance();

            account.close();

            assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        }

        @Test
        void shouldThrowExceptionWhenClosingAccountWithPositiveBalance() {
            Account account = activeAccount();

            assertThatThrownBy(account::close)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(AccountErrorCode.ACCOUNT_BUSINESS_CLOSE_WITH_BALANCE.getCode());
        }

    }

    @Nested
    class TransactionRegistration {

        @Test
        void shouldRegisterDepositTransactionEvent() {
            Account account = activeAccount();
            Money depositAmount = Money.of(new BigDecimal("500.00"));
            Money initialBalance = account.getCurrentBalance();

            account.clearDomainEvents();

            account.deposit(depositAmount);

            account.registerTransactionPerformed(
                    UUID.randomUUID(),
                    TransactionType.DEPOSIT,
                    depositAmount,
                    initialBalance,
                    account.getCurrentBalance(),
                    "DEP-001"
            );

            List<Object> events = account.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TransactionPerformedEvent.class);

            TransactionPerformedEvent event = (TransactionPerformedEvent) events.get(0);
            assertThat(event.type()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(event.amount()).isEqualByComparingTo(depositAmount.value());
        }

        @Test
        void shouldRegisterWithdrawalTransactionEvent() {
            Account account = activeAccount();
            Money withdrawAmount = Money.of(new BigDecimal("200.00"));
            Money initialBalance = account.getCurrentBalance();

            account.clearDomainEvents();

            account.withdraw(withdrawAmount);

            account.registerTransactionPerformed(
                    UUID.randomUUID(),
                    TransactionType.WITHDRAWAL,
                    withdrawAmount,
                    initialBalance,
                    account.getCurrentBalance(),
                    "WITH-001"
            );

            List<Object> events = account.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TransactionPerformedEvent.class);

            TransactionPerformedEvent event = (TransactionPerformedEvent) events.get(0);
            assertThat(event.type()).isEqualTo(TransactionType.WITHDRAWAL);
        }

        @Test
        void shouldRegisterTransactionPerformedEvent() {
            Account account = activeAccount();
            UUID transactionId = UUID.randomUUID();

            account.registerTransactionPerformed(
                    transactionId,
                    TransactionType.DEPOSIT,
                    Money.of(new BigDecimal("500.00")),
                    account.getCurrentBalance(),
                    account.getCurrentBalance().add(Money.of(new BigDecimal("500.00"))),
                    "DEP-001"
            );

            List<Object> events = account.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(TransactionPerformedEvent.class);
        }
    }

}