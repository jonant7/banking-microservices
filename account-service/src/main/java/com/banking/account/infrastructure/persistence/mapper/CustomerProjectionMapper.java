package com.banking.account.infrastructure.persistence.mapper;

import com.banking.account.domain.model.CustomerInfo;
import com.banking.account.domain.model.CustomerStatus;
import com.banking.account.infrastructure.persistence.entity.CustomerProjectionJpaEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CustomerProjectionMapper {

    public CustomerProjectionJpaEntity toEntity(CustomerInfo customerInfo, String customerIdValue, Instant eventOccurredAt) {
        CustomerProjectionJpaEntity entity = CustomerProjectionJpaEntity.builder()
                .customerIdValue(customerIdValue)
                .fullName(customerInfo.fullName())
                .status(customerInfo.status().name())
                .lastEventAt(eventOccurredAt)
                .build();

        entity.setId(customerInfo.customerId());

        return entity;
    }

    public CustomerInfo toDomain(CustomerProjectionJpaEntity entity) {
        return new CustomerInfo(
                entity.getId(),
                entity.getFullName(),
                CustomerStatus.valueOf(entity.getStatus())
        );
    }

    public void updateEntityFromDomain(CustomerInfo customerInfo, CustomerProjectionJpaEntity entity, Instant eventOccurredAt) {
        entity.setFullName(customerInfo.fullName());
        entity.setStatus(customerInfo.status().name());
        entity.setLastEventAt(eventOccurredAt);
    }

}