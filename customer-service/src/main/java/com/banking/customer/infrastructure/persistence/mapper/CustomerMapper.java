package com.banking.customer.infrastructure.persistence.mapper;

import com.banking.customer.domain.model.*;
import com.banking.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomerMapper {

    public Customer toDomain(CustomerJpaEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return Customer.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getLastName(),
                entity.getGender(),
                entity.getBirthDate(),
                Identification.of(entity.getIdentification()),
                entity.getAddress(),
                PhoneNumber.ofNullable(entity.getPhone()),
                CustomerId.of(entity.getCustomerId()),
                PasswordHash.fromHash(entity.getPasswordHash()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public CustomerJpaEntity toEntity(Customer domain) {
        if (Objects.isNull(domain)) {
            return null;
        }

        CustomerJpaEntity entity = new CustomerJpaEntity();
        mapDomainToEntity(domain, entity);
        return entity;
    }

    public void updateEntityFromDomain(Customer domain, CustomerJpaEntity entity) {
        if (Objects.isNull(domain) || Objects.isNull(entity)) {
            return;
        }
        mapDomainToEntity(domain, entity);
    }

    private void mapDomainToEntity(Customer domain, CustomerJpaEntity entity) {
        if (Objects.nonNull(domain.getId())) {
            entity.setId(domain.getId());
        }
        entity.setName(domain.getName());
        entity.setLastName(domain.getLastName());
        entity.setGender(domain.getGender());
        entity.setBirthDate(domain.getBirthDate());
        entity.setIdentification(domain.getIdentificationValue());
        entity.setAddress(domain.getAddress());
        entity.setPhone(domain.getPhoneValue());
        entity.setCustomerId(domain.getCustomerIdValue());
        entity.setPasswordHash(domain.getPasswordHash().value());
        entity.setStatus(domain.getStatus());
        if (Objects.nonNull(domain.getCreatedAt())) {
            entity.setCreatedAt(domain.getCreatedAt());
        }
        if (Objects.nonNull(domain.getUpdatedAt())) {
            entity.setUpdatedAt(domain.getUpdatedAt());
        }
    }

}