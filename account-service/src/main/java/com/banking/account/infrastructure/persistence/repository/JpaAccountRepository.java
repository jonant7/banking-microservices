package com.banking.account.infrastructure.persistence.repository;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.infrastructure.persistence.entity.AccountJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAccountRepository extends JpaRepository<AccountJpaEntity, UUID>, JpaSpecificationExecutor<AccountJpaEntity> {

    Optional<AccountJpaEntity> findByNumber(String number);

    List<AccountJpaEntity> findByCustomerId(UUID customerId);

    Page<AccountJpaEntity> findByCustomerId(UUID customerId, Pageable pageable);

    List<AccountJpaEntity> findByCustomerIdAndStatus(UUID customerId, AccountStatus status);

    Optional<AccountJpaEntity> findByIdAndCustomerId(UUID id, UUID customerId);

    boolean existsByNumber(String accountNumber);

}