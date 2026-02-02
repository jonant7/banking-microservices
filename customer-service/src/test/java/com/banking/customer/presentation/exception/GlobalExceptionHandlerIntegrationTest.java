package com.banking.customer.presentation.exception;

import com.banking.customer.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
@DisplayName("Global Exception Handler Integration Tests")
class GlobalExceptionHandlerIntegrationTest extends IntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private static final String BASE_PATH = "/api/v1/customers";
//
//    @Nested
//    @DisplayName("Validation Errors")
//    class ValidationErrorTests {
//
//        @Test
//        @DisplayName("Should return 400 with field errors for invalid request")
//        void shouldReturn400WithFieldErrors() throws Exception {
//            CreateCustomerApiRequest request = CustomerApiRequestMother.createInvalid();
//
//            mockMvc.perform(post(BASE_PATH)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.status").value(400))
//                    .andExpect(jsonPath("$.error").value("Bad Request"))
//                    .andExpect(jsonPath("$.fieldErrors").isArray())
//                    .andExpect(jsonPath("$.timestamp").exists())
//                    .andExpect(jsonPath("$.path").value(BASE_PATH));
//        }
//
//        @Test
//        @DisplayName("Should return specific field errors")
//        void shouldReturnSpecificFieldErrors() throws Exception {
//            CreateCustomerApiRequest request = CustomerApiRequestMother.createInvalid();
//
//            mockMvc.perform(post(BASE_PATH)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isBadRequest())
//                    .andExpect(jsonPath("$.fieldErrors[*].field", hasItems("name", "lastName")))
//                    .andExpect(jsonPath("$.fieldErrors[*].message", everyItem(notNullValue())));
//        }
//    }
//
//    @Nested
//    @DisplayName("Not Found Errors")
//    class NotFoundErrorTests {
//
//        @Test
//        @DisplayName("Should return 404 for non-existent customer")
//        void shouldReturn404ForNonExistentCustomer() throws Exception {
//            UUID nonExistentId = UUID.randomUUID();
//
//            mockMvc.perform(get(BASE_PATH + "/{id}", nonExistentId))
//                    .andExpect(status().isNotFound())
//                    .andExpect(jsonPath("$.status").value(404))
//                    .andExpect(jsonPath("$.error").value("Not Found"))
//                    .andExpect(jsonPath("$.message").exists());
//        }
//    }
//
//    @Nested
//    @DisplayName("Conflict Errors")
//    class ConflictErrorTests {
//
//        @Test
//        @DisplayName("Should return 409 for duplicate customer")
//        void shouldReturn409ForDuplicateCustomer() throws Exception {
//            CreateCustomerApiRequest request = CustomerApiRequestMother.createJoseLema();
//
//            mockMvc.perform(post(BASE_PATH)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isCreated());
//
//            mockMvc.perform(post(BASE_PATH)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(request)))
//                    .andExpect(status().isConflict())
//                    .andExpect(jsonPath("$.status").value(409))
//                    .andExpect(jsonPath("$.error").value("Conflict"))
//                    .andExpect(jsonPath("$.message").exists());
//        }
//    }
//
//    @Nested
//    @DisplayName("Method Not Allowed Errors")
//    class MethodNotAllowedTests {
//
//        @Test
//        @DisplayName("Should return 405 for unsupported HTTP method")
//        void shouldReturn405ForUnsupportedMethod() throws Exception {
//            mockMvc.perform(delete(BASE_PATH))
//                    .andExpect(status().isMethodNotAllowed())
//                    .andExpect(jsonPath("$.status").value(405))
//                    .andExpect(jsonPath("$.message").exists());
//        }
//    }

}