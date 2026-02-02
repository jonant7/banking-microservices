package com.banking.account.presentation.rest;

import com.banking.account.IntegrationTest;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.domain.model.*;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @MockitoBean
    private CustomerEventListener customerEventListener;

    private static final String BASE_PATH = "/api/v1/reports";

    @BeforeEach
    void setUp() {
        // Configurar MockMvc manualmente
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Nested
    class GenerateAccountStatementIntegration {

        @Test
        void shouldGenerateStatementWithAccountsAndTransactions() throws Exception {
            UUID customerId = UUID.randomUUID();
            String customerName = "Integration Test User";
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now().plusDays(1);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(customerEventListener.getCustomerName(customerId)).thenReturn(customerName);

            Account account = createAndSaveAccount("1234567890", customerId);
            createAndSaveTransaction(account, TransactionType.DEPOSIT, new BigDecimal("500.00"));
            createAndSaveTransaction(account, TransactionType.WITHDRAWAL, new BigDecimal("200.00"));

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.customerId").value(customerId.toString()))
                    .andExpect(jsonPath("$.data.customerName").value(customerName))
                    .andExpect(jsonPath("$.data.accounts").isArray())
                    .andExpect(jsonPath("$.data.accounts[0].account.accountNumber").value("1234567890"))
                    .andExpect(jsonPath("$.data.accounts[0].transactions").isArray());
        }

        @Test
        void shouldReturnEmptyAccountsForCustomerWithoutAccounts() throws Exception {
            UUID customerId = UUID.randomUUID();
            String customerName = "No Accounts User";
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now().plusDays(1);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(customerEventListener.getCustomerName(customerId)).thenReturn(customerName);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accounts").isEmpty());
        }

        @Test
        void shouldReturnEmptyTransactionsOutsideDateRange() throws Exception {
            UUID customerId = UUID.randomUUID();
            String customerName = "Date Range Test User";
            LocalDateTime startDate = LocalDateTime.now().minusDays(5);
            LocalDateTime endDate = LocalDateTime.now().minusDays(3);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(customerEventListener.getCustomerName(customerId)).thenReturn(customerName);

            Account account = createAndSaveAccount("5555555555", customerId);
            createAndSaveTransaction(account, TransactionType.DEPOSIT, new BigDecimal("1000.00"));

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accounts[0].transactions").isEmpty());
        }

        @Test
        void shouldFailWhenCustomerDoesNotExist() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();

            when(customerEventListener.customerExists(customerId)).thenReturn(false);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void shouldFailWhenCustomerIsInactive() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now();

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(false);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void shouldGenerateStatementWithMultipleAccounts() throws Exception {
            UUID customerId = UUID.randomUUID();
            String customerName = "Multi Account User";
            LocalDateTime startDate = LocalDateTime.now().minusDays(30);
            LocalDateTime endDate = LocalDateTime.now().plusDays(1);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);
            when(customerEventListener.getCustomerName(customerId)).thenReturn(customerName);

            Account savingsAccount = createAndSaveAccount("1111111111", customerId, AccountType.SAVINGS);
            Account checkingAccount = createAndSaveAccount("2222222222", customerId, AccountType.CHECKING);

            createAndSaveTransaction(savingsAccount, TransactionType.DEPOSIT, new BigDecimal("1000.00"));
            createAndSaveTransaction(checkingAccount, TransactionType.DEPOSIT, new BigDecimal("2000.00"));

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accounts.length()").value(2));
        }

        @Test
        void shouldReturn400WhenStartDateIsAfterEndDate() throws Exception {
            UUID customerId = UUID.randomUUID();
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.now().minusDays(30);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);

            mockMvc.perform(get(BASE_PATH)
                            .param("customerId", customerId.toString())
                            .param("startDate", startDate.toString())
                            .param("endDate", endDate.toString()))
                    .andExpect(status().isBadRequest());
        }
    }

    private Account createAndSaveAccount(String accountNumber, UUID customerId) {
        return createAndSaveAccount(accountNumber, customerId, AccountType.SAVINGS);
    }

    private Account createAndSaveAccount(String accountNumber, UUID customerId, AccountType accountType) {
        Account account = Account.create(
                accountNumber,
                accountType,
                new BigDecimal("1000.00"),
                customerId
        );
        account.clearDomainEvents();
        return accountRepository.save(account);
    }

    private void createAndSaveTransaction(Account account, TransactionType type, BigDecimal amount) {
        Money amountMoney = Money.of(amount);
        Money balanceBefore = account.getCurrentBalance();
        Money balanceAfter = type == TransactionType.DEPOSIT
                ? balanceBefore.add(amountMoney)
                : balanceBefore.subtract(amountMoney);

        Transaction transaction = Transaction.create(
                type,
                amountMoney,
                balanceBefore,
                balanceAfter,
                "REF-" + UUID.randomUUID().toString().substring(0, 8),
                account.getId()
        );
        transactionRepository.save(transaction);
    }

}