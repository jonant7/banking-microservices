package com.banking.customer.presentation.rest;

import com.banking.customer.IntegrationTest;
import com.banking.customer.domain.model.CustomerStatus;
import com.banking.customer.presentation.dto.request.CreateCustomerApiRequest;
import com.banking.customer.presentation.dto.request.PatchCustomerApiRequest;
import com.banking.customer.presentation.dto.request.UpdateCustomerApiRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.UUID;

import static com.banking.customer.fixtures.mothers.CustomerApiRequestMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.base-path}")
    private String basePath;

    private MockMvc mockMvc;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
        baseUrl = basePath + "/customers";
    }

    @Nested
    @DisplayName("POST /customers - Create Customer")
    class CreateCustomerTests {

        @Test
        @DisplayName("Should create customer successfully with valid data")
        void shouldCreateCustomerSuccessfully() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withCustomerId("CUST" + System.currentTimeMillis())
                    .withIdentification(String.valueOf(System.currentTimeMillis()))
                    .build();

            MvcResult result = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Customer created successfully"))
                    .andExpect(jsonPath("$.data.name").value(request.getName()))
                    .andExpect(jsonPath("$.data.lastName").value(request.getLastName()))
                    .andExpect(jsonPath("$.data.customerId").value(request.getCustomerId()))
                    .andExpect(jsonPath("$.data.identification").value(request.getIdentification()))
                    .andExpect(jsonPath("$.data.gender").value(request.getGender().toString()))
                    .andExpect(jsonPath("$.data.status").value(true))
                    .andExpect(jsonPath("$.data.id").exists())
                    .andExpect(jsonPath("$.data.createdAt").exists())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andReturn();

            String content = result.getResponse().getContentAsString();
            String customerIdStr = objectMapper.readTree(content).get("data").get("id").asText();
            String fullName = objectMapper.readTree(content).get("data").get("fullName").asText();

            assertThat(customerIdStr).isNotNull();
            assertThat(fullName).isEqualTo(request.getName() + " " + request.getLastName());
        }

        @Test
        @DisplayName("Should create female customer successfully")
        void shouldCreateFemaleCustomer() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .female()
                    .withCustomerId("CUSTF" + System.currentTimeMillis())
                    .withIdentification("IDF" + System.currentTimeMillis())
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.gender").value("FEMALE"))
                    .andExpect(jsonPath("$.data.name").value("Jane"));
        }

        @Test
        @DisplayName("Should fail when name is blank")
        void shouldFailWhenNameIsBlank() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withName("")
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.fieldErrors").isArray())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'name')]").exists());
        }

        @Test
        @DisplayName("Should fail when name is too short")
        void shouldFailWhenNameIsTooShort() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withName("J")
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'name')]").exists());
        }

        @Test
        @DisplayName("Should fail when phone has invalid format")
        void shouldFailWhenPhoneHasInvalidFormat() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withPhone("123abc")
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'phone')]").exists());
        }

        @Test
        @DisplayName("Should fail when birth date is in the future")
        void shouldFailWhenBirthDateIsInFuture() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withBirthDate(LocalDate.now().plusDays(1))
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'birthDate')]").exists());
        }

        @Test
        @DisplayName("Should fail when gender is null")
        void shouldFailWhenGenderIsNull() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withGender(null)
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'gender')]").exists());
        }

        @Test
        @DisplayName("Should fail when identification is too short")
        void shouldFailWhenIdentificationIsTooShort() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withIdentification("123")
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'identification')]").exists());
        }

        @Test
        @DisplayName("Should fail when password is too short")
        void shouldFailWhenPasswordIsTooShort() throws Exception {
            CreateCustomerApiRequest request = createCustomer()
                    .withPassword("123")
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password')]").exists());
        }

        @Test
        @DisplayName("Should fail with duplicate customerId")
        void shouldFailWithDuplicateCustomerId() throws Exception {
            String customerId = "DUPLICATE" + System.currentTimeMillis();

            CreateCustomerApiRequest firstRequest = createCustomer()
                    .withCustomerId(customerId)
                    .withIdentification("ID1" + System.currentTimeMillis())
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(firstRequest)))
                    .andExpect(status().isCreated());

            CreateCustomerApiRequest duplicateRequest = createCustomer()
                    .withCustomerId(customerId)
                    .withIdentification("ID2" + System.currentTimeMillis())
                    .build();

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @DisplayName("Should fail with malformed JSON")
        void shouldFailWithMalformedJson() throws Exception {
            String malformedJson = "{\"name\": \"John\", invalid}";

            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid request format"));
        }
    }

    @Nested
    @DisplayName("GET /customers - Get All Customers")
    class GetAllCustomersTests {

        @Test
        @DisplayName("Should get all customers with default pagination")
        void shouldGetAllCustomersWithDefaultPagination() throws Exception {
            mockMvc.perform(get(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.page").isNumber())
                    .andExpect(jsonPath("$.size").isNumber())
                    .andExpect(jsonPath("$.totalElements").isNumber())
                    .andExpect(jsonPath("$.totalPages").isNumber())
                    .andExpect(jsonPath("$.first").isBoolean())
                    .andExpect(jsonPath("$.last").isBoolean());
        }

        @Test
        @DisplayName("Should get customers with custom pagination")
        void shouldGetCustomersWithCustomPagination() throws Exception {
            mockMvc.perform(get(baseUrl)
                            .param("page", "0")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(5));
        }

        @Test
        @DisplayName("Should get customers with sorting")
        void shouldGetCustomersWithSorting() throws Exception {
            mockMvc.perform(get(baseUrl)
                            .param("sortBy", "name")
                            .param("sortDirection", "ASC")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Should filter customers by status")
        void shouldFilterCustomersByStatus() throws Exception {
            mockMvc.perform(get(baseUrl)
                            .param("status", CustomerStatus.ACTIVE.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }

    @Nested
    @DisplayName("GET /customers/{id} - Get Customer By Id")
    class GetCustomerByIdTests {

        @Test
        @DisplayName("Should get customer by id successfully")
        void shouldGetCustomerByIdSuccessfully() throws Exception {
            CreateCustomerApiRequest createRequest = createCustomer()
                    .withCustomerId("GETTEST" + System.currentTimeMillis())
                    .withIdentification("IDGET" + System.currentTimeMillis())
                    .build();

            MvcResult createResult = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").exists())
                    .andReturn();

            String responseContent = createResult.getResponse().getContentAsString();
            String customerIdStr = objectMapper.readTree(responseContent).get("data").get("id").asText();
            UUID customerId = UUID.fromString(customerIdStr);

            mockMvc.perform(get(baseUrl + "/{id}", customerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(customerId.toString()))
                    .andExpect(jsonPath("$.data.name").value(createRequest.getName()))
                    .andExpect(jsonPath("$.data.customerId").value(createRequest.getCustomerId()));
        }

        @Test
        @DisplayName("Should return 404 when customer not found")
        void shouldReturn404WhenCustomerNotFound() throws Exception {
            UUID nonExistentId = UUID.randomUUID();

            mockMvc.perform(get(baseUrl + "/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Should fail with invalid UUID format")
        void shouldFailWithInvalidUuidFormat() throws Exception {
            mockMvc.perform(get(baseUrl + "/{id}", "invalid-uuid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /customers/{id} - Update Customer")
    class UpdateCustomerTests {

        private UUID createdCustomerId;

        @BeforeEach
        void createCustomerForUpdate() throws Exception {
            CreateCustomerApiRequest createRequest = createCustomer()
                    .withCustomerId("UPDATETEST" + System.currentTimeMillis())
                    .withIdentification("IDUPD" + System.currentTimeMillis())
                    .build();

            MvcResult result = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").exists())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            String customerIdStr = objectMapper.readTree(responseContent).get("data").get("id").asText();
            createdCustomerId = UUID.fromString(customerIdStr);
        }

        @Test
        @DisplayName("Should update customer successfully")
        void shouldUpdateCustomerSuccessfully() throws Exception {
            UpdateCustomerApiRequest updateRequest = updateCustomer()
                    .withName("UpdatedName")
                    .withLastName("UpdatedLastName")
                    .withAddress("New Address 123")
                    .build();

            mockMvc.perform(put(baseUrl + "/{id}", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Customer updated successfully"))
                    .andExpect(jsonPath("$.data.name").value("UpdatedName"))
                    .andExpect(jsonPath("$.data.lastName").value("UpdatedLastName"))
                    .andExpect(jsonPath("$.data.address").value("New Address 123"))
                    .andExpect(jsonPath("$.data.updatedAt").exists());
        }

        @Test
        @DisplayName("Should fail when updating with blank name")
        void shouldFailWhenUpdatingWithBlankName() throws Exception {
            UpdateCustomerApiRequest updateRequest = updateCustomer()
                    .withName("")
                    .build();

            mockMvc.perform(put(baseUrl + "/{id}", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'name')]").exists());
        }

        @Test
        @DisplayName("Should fail when updating non-existent customer")
        void shouldFailWhenUpdatingNonExistentCustomer() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            UpdateCustomerApiRequest updateRequest = updateCustomer().build();

            mockMvc.perform(put(baseUrl + "/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /customers/{id} - Patch Customer")
    class PatchCustomerTests {

        private UUID createdCustomerId;

        @BeforeEach
        void createCustomerForPatch() throws Exception {
            CreateCustomerApiRequest createRequest = createCustomer()
                    .withCustomerId("PATCHTEST" + System.currentTimeMillis())
                    .withIdentification("IDPATCH" + System.currentTimeMillis())
                    .build();

            MvcResult result = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").exists())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            String customerIdStr = objectMapper.readTree(responseContent).get("data").get("id").asText();
            createdCustomerId = UUID.fromString(customerIdStr);
        }

        @Test
        @DisplayName("Should patch only address")
        void shouldPatchOnlyAddress() throws Exception {
            PatchCustomerApiRequest patchRequest = patchCustomer()
                    .onlyAddress()
                    .build();

            mockMvc.perform(patch(baseUrl + "/{id}", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Customer updated successfully"))
                    .andExpect(jsonPath("$.data.address").value(patchRequest.getAddress()));
        }

        @Test
        @DisplayName("Should patch only phone")
        void shouldPatchOnlyPhone() throws Exception {
            PatchCustomerApiRequest patchRequest = patchCustomer()
                    .onlyPhone()
                    .build();

            mockMvc.perform(patch(baseUrl + "/{id}", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.phone").value(patchRequest.getPhone()));
        }

        @Test
        @DisplayName("Should patch only password")
        void shouldPatchOnlyPassword() throws Exception {
            PatchCustomerApiRequest patchRequest = patchCustomer()
                    .onlyPassword()
                    .build();

            mockMvc.perform(patch(baseUrl + "/{id}", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should patch multiple fields")
        void shouldPatchMultipleFields() throws Exception {
            PatchCustomerApiRequest patchRequest = patchCustomer()
                    .withAddress("Multi-patch Address")
                    .withPhone("9998887777")
                    .build();

            mockMvc.perform(patch(baseUrl + "/{id}", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.address").value("Multi-patch Address"))
                    .andExpect(jsonPath("$.data.phone").value("9998887777"));
        }

        @Test
        @DisplayName("Should fail when patching with invalid phone format")
        void shouldFailWhenPatchingWithInvalidPhone() throws Exception {
            PatchCustomerApiRequest patchRequest = patchCustomer()
                    .withPhone("invalid")
                    .build();

            mockMvc.perform(patch(baseUrl + "/{id}", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'phone')]").exists());
        }
    }

    @Nested
    @DisplayName("PATCH /customers/{id}/activate - Activate Customer")
    class ActivateCustomerTests {

        private UUID createdCustomerId;

        @BeforeEach
        void createCustomerForActivation() throws Exception {
            CreateCustomerApiRequest createRequest = createCustomer()
                    .withCustomerId("ACTIVATE" + System.currentTimeMillis())
                    .withIdentification("IDACT" + System.currentTimeMillis())
                    .build();

            MvcResult result = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").exists())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            String customerIdStr = objectMapper.readTree(responseContent).get("data").get("id").asText();
            createdCustomerId = UUID.fromString(customerIdStr);
        }

        @Test
        @DisplayName("Should activate customer successfully")
        void shouldActivateCustomerSuccessfully() throws Exception {
            mockMvc.perform(patch(baseUrl + "/{id}/activate", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Customer activated successfully"))
                    .andExpect(jsonPath("$.data.status").value(true));
        }

        @Test
        @DisplayName("Should fail to activate non-existent customer")
        void shouldFailToActivateNonExistentCustomer() throws Exception {
            UUID nonExistentId = UUID.randomUUID();

            mockMvc.perform(patch(baseUrl + "/{id}/activate", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /customers/{id}/deactivate - Deactivate Customer")
    class DeactivateCustomerTests {

        private UUID createdCustomerId;

        @BeforeEach
        void createCustomerForDeactivation() throws Exception {
            CreateCustomerApiRequest createRequest = createCustomer()
                    .withCustomerId("DEACTIVATE" + System.currentTimeMillis())
                    .withIdentification("IDDEACT" + System.currentTimeMillis())
                    .build();

            MvcResult result = mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").exists())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            String customerIdStr = objectMapper.readTree(responseContent).get("data").get("id").asText();
            createdCustomerId = UUID.fromString(customerIdStr);
        }

        @Test
        @DisplayName("Should deactivate customer successfully")
        void shouldDeactivateCustomerSuccessfully() throws Exception {
            mockMvc.perform(patch(baseUrl + "/{id}/deactivate", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Customer deactivated successfully"))
                    .andExpect(jsonPath("$.data.status").value(false));
        }

        @Test
        @DisplayName("Should fail to deactivate non-existent customer")
        void shouldFailToDeactivateNonExistentCustomer() throws Exception {
            UUID nonExistentId = UUID.randomUUID();

            mockMvc.perform(patch(baseUrl + "/{id}/deactivate", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should deactivate and then reactivate customer")
        void shouldDeactivateAndReactivateCustomer() throws Exception {
            mockMvc.perform(patch(baseUrl + "/{id}/deactivate", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value(false));

            mockMvc.perform(patch(baseUrl + "/{id}/activate", createdCustomerId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value(true));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle unsupported media type")
        void shouldHandleUnsupportedMediaType() throws Exception {
            mockMvc.perform(post(baseUrl)
                            .contentType(MediaType.APPLICATION_XML)
                            .content("<xml>test</xml>"))
                    .andExpect(status().isUnsupportedMediaType())
                    .andExpect(jsonPath("$.status").value(415));
        }

        @Test
        @DisplayName("Should handle method not allowed")
        void shouldHandleMethodNotAllowed() throws Exception {
            mockMvc.perform(delete(baseUrl)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(jsonPath("$.status").value(405));
        }
    }
}