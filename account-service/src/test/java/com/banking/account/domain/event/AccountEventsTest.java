package com.banking.account.domain.event;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.TransactionType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountEventsTest {

    @Nested
    class AccountCreatedEventTest {

        @Test
        void shouldCreateEventWithAllFields() {
            UUID accountId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            LocalDateTime occurredAt = LocalDateTime.now();

            AccountCreatedEvent event = AccountCreatedEvent.builder()
                    .accountId(accountId)
                    .accountNumber("1234567890")
                    .customerId(customerId)
                    .accountType(AccountType.SAVINGS)
                    .initialBalance(new BigDecimal("1000.00"))
                    .status(AccountStatus.ACTIVE)
                    .occurredAt(occurredAt)
                    .build();

            assertThat(event).isNotNull();
            assertThat(event.accountId()).isEqualTo(accountId);
            assertThat(event.accountNumber()).isEqualTo("1234567890");
            assertThat(event.customerId()).isEqualTo(customerId);
            assertThat(event.accountType()).isEqualTo(AccountType.SAVINGS);
            assertThat(event.initialBalance()).isEqualByComparingTo("1000.00");
            assertThat(event.status()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        void shouldCreateEventWithCheckingAccount() {
            AccountCreatedEvent event = AccountCreatedEvent.builder()
                    .accountId(UUID.randomUUID())
                    .accountNumber("0987654321")
                    .customerId(UUID.randomUUID())
                    .accountType(AccountType.CHECKING)
                    .initialBalance(new BigDecimal("500.00"))
                    .status(AccountStatus.ACTIVE)
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.accountType()).isEqualTo(AccountType.CHECKING);
        }

        @Test
        void shouldCreateEventWithInactiveStatus() {
            AccountCreatedEvent event = AccountCreatedEvent.builder()
                    .accountId(UUID.randomUUID())
                    .accountNumber("123456")
                    .customerId(UUID.randomUUID())
                    .accountType(AccountType.SAVINGS)
                    .initialBalance(new BigDecimal("100.00"))
                    .status(AccountStatus.INACTIVE)
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.status()).isEqualTo(AccountStatus.INACTIVE);
        }
    }

    @Nested
    class TransactionPerformedEventTest {

        @Test
        void shouldCreateDepositEvent() {
            UUID transactionId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            LocalDateTime occurredAt = LocalDateTime.now();

            TransactionPerformedEvent event = TransactionPerformedEvent.builder()
                    .transactionId(transactionId)
                    .accountId(accountId)
                    .accountNumber("1234567890")
                    .customerId(customerId)
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("500.00"))
                    .balanceBefore(new BigDecimal("1000.00"))
                    .balanceAfter(new BigDecimal("1500.00"))
                    .reference("DEP-001")
                    .occurredAt(occurredAt)
                    .build();

            assertThat(event).isNotNull();
            assertThat(event.transactionId()).isEqualTo(transactionId);
            assertThat(event.accountId()).isEqualTo(accountId);
            assertThat(event.accountNumber()).isEqualTo("1234567890");
            assertThat(event.customerId()).isEqualTo(customerId);
            assertThat(event.type()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(event.amount()).isEqualByComparingTo("500.00");
            assertThat(event.balanceBefore()).isEqualByComparingTo("1000.00");
            assertThat(event.balanceAfter()).isEqualByComparingTo("1500.00");
            assertThat(event.reference()).isEqualTo("DEP-001");
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        }

        @Test
        void shouldCreateWithdrawalEvent() {
            TransactionPerformedEvent event = TransactionPerformedEvent.builder()
                    .transactionId(UUID.randomUUID())
                    .accountId(UUID.randomUUID())
                    .accountNumber("123456")
                    .customerId(UUID.randomUUID())
                    .type(TransactionType.WITHDRAWAL)
                    .amount(new BigDecimal("200.00"))
                    .balanceBefore(new BigDecimal("1000.00"))
                    .balanceAfter(new BigDecimal("800.00"))
                    .reference("WITH-001")
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.type()).isEqualTo(TransactionType.WITHDRAWAL);
        }

        @Test
        void shouldAllowNullReference() {
            TransactionPerformedEvent event = TransactionPerformedEvent.builder()
                    .transactionId(UUID.randomUUID())
                    .accountId(UUID.randomUUID())
                    .accountNumber("123456")
                    .customerId(UUID.randomUUID())
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("100.00"))
                    .balanceBefore(new BigDecimal("500.00"))
                    .balanceAfter(new BigDecimal("600.00"))
                    .reference(null)
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event.reference()).isNull();
        }
    }

    @Nested
    class EventTimestamps {

        @Test
        void shouldCaptureOccurredAtTimestamp() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            AccountCreatedEvent event = AccountCreatedEvent.builder()
                    .accountId(UUID.randomUUID())
                    .accountNumber("123456")
                    .customerId(UUID.randomUUID())
                    .accountType(AccountType.SAVINGS)
                    .initialBalance(new BigDecimal("100.00"))
                    .status(AccountStatus.ACTIVE)
                    .occurredAt(LocalDateTime.now())
                    .build();

            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(event.occurredAt()).isAfter(before);
            assertThat(event.occurredAt()).isBefore(after);
        }

        @Test
        void shouldAllowSpecificTimestamp() {
            LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

            TransactionPerformedEvent event = TransactionPerformedEvent.builder()
                    .transactionId(UUID.randomUUID())
                    .accountId(UUID.randomUUID())
                    .accountNumber("123456")
                    .customerId(UUID.randomUUID())
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("100.00"))
                    .balanceBefore(new BigDecimal("500.00"))
                    .balanceAfter(new BigDecimal("600.00"))
                    .occurredAt(specificTime)
                    .build();

            assertThat(event.occurredAt()).isEqualTo(specificTime);
        }
    }

    @Nested
    class EventEquality {

        @Test
        void shouldBeEqualWhenSameData() {
            UUID transactionId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            LocalDateTime occurredAt = LocalDateTime.now();

            TransactionPerformedEvent event1 = TransactionPerformedEvent.builder()
                    .transactionId(transactionId)
                    .accountId(accountId)
                    .accountNumber("123456")
                    .customerId(UUID.randomUUID())
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("100.00"))
                    .balanceBefore(new BigDecimal("500.00"))
                    .balanceAfter(new BigDecimal("600.00"))
                    .occurredAt(occurredAt)
                    .build();

            TransactionPerformedEvent event2 = TransactionPerformedEvent.builder()
                    .transactionId(transactionId)
                    .accountId(accountId)
                    .accountNumber("123456")
                    .customerId(event1.customerId())
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("100.00"))
                    .balanceBefore(new BigDecimal("500.00"))
                    .balanceAfter(new BigDecimal("600.00"))
                    .occurredAt(occurredAt)
                    .build();

            assertThat(event1).isEqualTo(event2);
        }

        @Test
        void shouldNotBeEqualWhenDifferentData() {
            TransactionPerformedEvent event1 = TransactionPerformedEvent.builder()
                    .transactionId(UUID.randomUUID())
                    .accountId(UUID.randomUUID())
                    .accountNumber("123456")
                    .customerId(UUID.randomUUID())
                    .type(TransactionType.DEPOSIT)
                    .amount(new BigDecimal("100.00"))
                    .balanceBefore(new BigDecimal("500.00"))
                    .balanceAfter(new BigDecimal("600.00"))
                    .occurredAt(LocalDateTime.now())
                    .build();

            TransactionPerformedEvent event2 = TransactionPerformedEvent.builder()
                    .transactionId(UUID.randomUUID())
                    .accountId(UUID.randomUUID())
                    .accountNumber("654321")
                    .customerId(UUID.randomUUID())
                    .type(TransactionType.WITHDRAWAL)
                    .amount(new BigDecimal("200.00"))
                    .balanceBefore(new BigDecimal("1000.00"))
                    .balanceAfter(new BigDecimal("800.00"))
                    .occurredAt(LocalDateTime.now())
                    .build();

            assertThat(event1).isNotEqualTo(event2);
        }
    }
}