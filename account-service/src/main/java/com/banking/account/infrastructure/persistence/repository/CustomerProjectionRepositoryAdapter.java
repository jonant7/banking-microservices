package com.banking.account.infrastructure.persistence.repository;

import com.banking.account.domain.model.CustomerInfo;
import com.banking.account.domain.repository.CustomerProjectionRepository;
import com.banking.account.infrastructure.persistence.entity.CustomerProjectionJpaEntity;
import com.banking.account.infrastructure.persistence.mapper.CustomerProjectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerProjectionRepositoryAdapter implements CustomerProjectionRepository {

    private final JpaCustomerProjectionRepository jpaRepository;
    private final CustomerProjectionMapper mapper;

    @Override
    @Transactional
    public void save(CustomerInfo customerInfo) {
        jpaRepository.findById(customerInfo.customerId())
                .ifPresentOrElse(
                        entity -> mapper.updateEntityFromDomain(customerInfo, entity, Instant.now()),
                        () -> {
                            CustomerProjectionJpaEntity entity = mapper.toEntity(
                                    customerInfo,
                                    customerInfo.customerId().toString(),
                                    Instant.now()
                            );
                            jpaRepository.save(entity);
                        }
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerInfo> findById(UUID customerId) {
        return jpaRepository.findById(customerId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID customerId) {
        return jpaRepository.existsById(customerId);
    }

}