package com.banking.account.presentation.rest;

import com.banking.account.IntegrationTest;
import com.banking.account.application.port.out.CustomerEventListener;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @MockitoBean
    private CustomerEventListener customerEventListener;

    private static final String BASE_PATH = "/api/v1/accounts";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Nested
    class CreateAccountIntegration {

        @Test
        void shouldCreateAccountAndPersistInDatabase() throws Exception {
            UUID customerId = UUID.randomUUID();

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);

            String requestJson = """
                    {
                        "accountNumber": "9876543210",
                        "accountType": "SAVINGS",
                        "initialBalance": 2500.00,
                        "customerId": "%s"
                    }
                    """.formatted(customerId);

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accountNumber").value("9876543210"))
                    .andExpect(jsonPath("$.data.initialBalance").value(2500.00))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));

            assertThat(accountRepository.findAll(null, Pageable.unpaged())).hasSize(1);
        }

        @Test
        void shouldReturn400WhenCustomerIsInactive() throws Exception {
            UUID customerId = UUID.randomUUID();

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(false);

            String requestJson = """
                    {
                        "accountNumber": "1111111111",
                        "accountType": "CHECKING",
                        "initialBalance": 100.00,
                        "customerId": "%s"
                    }
                    """.formatted(customerId);

            mockMvc.perform(post(BASE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isForbidden());

            assertThat(accountRepository.findAll(null, Pageable.unpaged())).isEmpty();
        }
    }

    @Nested
    class GetAccountIntegration {

        @Test
        void shouldRetrieveExistingAccount() throws Exception {
            UUID customerId = UUID.randomUUID();
            Account account = createAndSaveAccount("5555555555", customerId);

            mockMvc.perform(get(BASE_PATH + "/{id}", account.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(account.getId().toString()))
                    .andExpect(jsonPath("$.data.accountNumber").value("5555555555"));
        }

        @Test
        void shouldRetrieveAccountByAccountNumber() throws Exception {
            UUID customerId = UUID.randomUUID();
            createAndSaveAccount("6666666666", customerId);

            mockMvc.perform(get(BASE_PATH + "/number/{accountNumber}", "6666666666"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accountNumber").value("6666666666"));
        }

        @Test
        void shouldReturn404ForNonExistentAccount() throws Exception {
            UUID nonExistentId = UUID.randomUUID();

            mockMvc.perform(get(BASE_PATH + "/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class TransactionIntegration {

        @Test
        void shouldExecuteDepositAndUpdateBalance() throws Exception {
            UUID customerId = UUID.randomUUID();
            Account account = createAndSaveAccount("7777777777", customerId);

            when(customerEventListener.customerExists(any())).thenReturn(true);
            when(customerEventListener.isCustomerActive(any())).thenReturn(true);

            String depositRequest = """
                    {
                        "type": "DEPOSIT",
                        "amount": 500.00,
                        "reference": "TEST-DEP-001"
                    }
                    """;

            mockMvc.perform(post(BASE_PATH + "/{accountId}/transactions", account.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(depositRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.type").value("DEPOSIT"))
                    .andExpect(jsonPath("$.data.amount").value(500.00))
                    .andExpect(jsonPath("$.data.balanceAfter").value(1500.00));

            Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
            assertThat(updatedAccount.getCurrentBalance().value()).isEqualByComparingTo("1500.00");
        }

        @Test
        void shouldExecuteWithdrawalAndUpdateBalance() throws Exception {
            UUID customerId = UUID.randomUUID();
            Account account = createAndSaveAccount("8888888888", customerId);

            String withdrawalRequest = """
                    {
                        "type": "WITHDRAWAL",
                        "amount": 300.00,
                        "reference": "TEST-WITH-001"
                    }
                    """;

            mockMvc.perform(post(BASE_PATH + "/{accountId}/transactions", account.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(withdrawalRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.type").value("WITHDRAWAL"))
                    .andExpect(jsonPath("$.data.balanceAfter").value(700.00));

            Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
            assertThat(updatedAccount.getCurrentBalance().value()).isEqualByComparingTo("700.00");
        }

        @Test
        void shouldReturn400WhenWithdrawalExceedsBalance() throws Exception {
            UUID customerId = UUID.randomUUID();
            Account account = createAndSaveAccount("9999999999", customerId);

            String withdrawalRequest = """
                    {
                        "type": "WITHDRAWAL",
                        "amount": 5000.00,
                        "reference": "TEST-WITH-002"
                    }
                    """;

            mockMvc.perform(post(BASE_PATH + "/{accountId}/transactions", account.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(withdrawalRequest))
                    .andExpect(status().isBadRequest());

            Account unchangedAccount = accountRepository.findById(account.getId()).orElseThrow();
            assertThat(unchangedAccount.getCurrentBalance().value()).isEqualByComparingTo("1000.00");
        }
    }

    @Nested
    class AccountStatusIntegration {

        @Test
        void shouldActivateDeactivatedAccount() throws Exception {
            UUID customerId = UUID.randomUUID();
            Account account = createAndSaveAccount("1010101010", customerId);
            account.deactivate();
            accountRepository.save(account);

            when(customerEventListener.customerExists(customerId)).thenReturn(true);
            when(customerEventListener.isCustomerActive(customerId)).thenReturn(true);

            mockMvc.perform(patch(BASE_PATH + "/{id}/activate", account.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));
        }

        @Test
        void shouldDeactivateActiveAccount() throws Exception {
            UUID customerId = UUID.randomUUID();
            Account account = createAndSaveAccount("2020202020", customerId);

            mockMvc.perform(patch(BASE_PATH + "/{id}/deactivate", account.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("INACTIVE"));
        }
    }

    private Account createAndSaveAccount(String accountNumber, UUID customerId) {
        Account account = Account.create(
                accountNumber,
                AccountType.SAVINGS,
                new BigDecimal("1000.00"),
                customerId
        );
        account.clearDomainEvents();
        return accountRepository.save(account);
    }

}