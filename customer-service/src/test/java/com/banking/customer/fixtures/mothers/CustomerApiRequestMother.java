package com.banking.customer.fixtures.mothers;

import com.banking.customer.domain.model.Gender;
import com.banking.customer.presentation.dto.request.CreateCustomerApiRequest;
import com.banking.customer.presentation.dto.request.PatchCustomerApiRequest;
import com.banking.customer.presentation.dto.request.UpdateCustomerApiRequest;

import java.time.LocalDate;

public class CustomerApiRequestMother {

    public static CreateCustomerApiRequestBuilder createCustomer() {
        return new CreateCustomerApiRequestBuilder();
    }

    public static UpdateCustomerApiRequestBuilder updateCustomer() {
        return new UpdateCustomerApiRequestBuilder();
    }

    public static PatchCustomerApiRequestBuilder patchCustomer() {
        return new PatchCustomerApiRequestBuilder();
    }

    public static class CreateCustomerApiRequestBuilder {
        private String name = "John";
        private String lastName = "Doe";
        private Gender gender = Gender.MALE;
        private LocalDate birthDate = LocalDate.of(1990, 1, 15);
        private String identification = "1234567890";
        private String address = "123 Main St, New York, NY 10001";
        private String phone = "1234567890";
        private String customerId = "CUST001";
        private String password = "securePass123";

        public CreateCustomerApiRequestBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CreateCustomerApiRequestBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public CreateCustomerApiRequestBuilder withGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public CreateCustomerApiRequestBuilder withBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public CreateCustomerApiRequestBuilder withIdentification(String identification) {
            this.identification = identification;
            return this;
        }

        public CreateCustomerApiRequestBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public CreateCustomerApiRequestBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public CreateCustomerApiRequestBuilder withCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public CreateCustomerApiRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public CreateCustomerApiRequestBuilder female() {
            this.gender = Gender.FEMALE;
            this.name = "Jane";
            return this;
        }

        public CreateCustomerApiRequestBuilder minor() {
            this.birthDate = LocalDate.now().minusYears(15);
            return this;
        }

        public CreateCustomerApiRequestBuilder senior() {
            this.birthDate = LocalDate.of(1950, 5, 20);
            return this;
        }

        public CreateCustomerApiRequest build() {
            CreateCustomerApiRequest request = new CreateCustomerApiRequest();
            request.setName(name);
            request.setLastName(lastName);
            request.setGender(gender);
            request.setBirthDate(birthDate);
            request.setIdentification(identification);
            request.setAddress(address);
            request.setPhone(phone);
            request.setCustomerId(customerId);
            request.setPassword(password);
            return request;
        }
    }

    public static class UpdateCustomerApiRequestBuilder {
        private String name = "John";
        private String lastName = "Doe";
        private Gender gender = Gender.MALE;
        private LocalDate birthDate = LocalDate.of(1990, 1, 15);
        private String address = "456 Oak Ave, Brooklyn, NY 11201";
        private String phone = "9876543210";
        private String password = "newPassword456";

        public UpdateCustomerApiRequestBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UpdateCustomerApiRequestBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UpdateCustomerApiRequestBuilder withGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public UpdateCustomerApiRequestBuilder withBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public UpdateCustomerApiRequestBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public UpdateCustomerApiRequestBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public UpdateCustomerApiRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UpdateCustomerApiRequest build() {
            UpdateCustomerApiRequest request = new UpdateCustomerApiRequest();
            request.setName(name);
            request.setLastName(lastName);
            request.setGender(gender);
            request.setBirthDate(birthDate);
            request.setAddress(address);
            request.setPhone(phone);
            request.setPassword(password);
            return request;
        }
    }

    public static class PatchCustomerApiRequestBuilder {
        private String address;
        private String phone;
        private String password;
        private Boolean status;

        public PatchCustomerApiRequestBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public PatchCustomerApiRequestBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public PatchCustomerApiRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public PatchCustomerApiRequestBuilder withStatus(Boolean status) {
            this.status = status;
            return this;
        }

        public PatchCustomerApiRequestBuilder onlyAddress() {
            this.address = "789 Pine Rd, Queens, NY 11354";
            this.phone = null;
            this.password = null;
            this.status = null;
            return this;
        }

        public PatchCustomerApiRequestBuilder onlyPhone() {
            this.address = null;
            this.phone = "5551234567";
            this.password = null;
            this.status = null;
            return this;
        }

        public PatchCustomerApiRequestBuilder onlyPassword() {
            this.address = null;
            this.phone = null;
            this.password = "patchedPass789";
            this.status = null;
            return this;
        }

        public PatchCustomerApiRequestBuilder onlyStatus(boolean status) {
            this.address = null;
            this.phone = null;
            this.password = null;
            this.status = status;
            return this;
        }

        public PatchCustomerApiRequest build() {
            PatchCustomerApiRequest request = new PatchCustomerApiRequest();
            request.setAddress(address);
            request.setPhone(phone);
            request.setPassword(password);
            request.setStatus(status);
            return request;
        }
    }

}