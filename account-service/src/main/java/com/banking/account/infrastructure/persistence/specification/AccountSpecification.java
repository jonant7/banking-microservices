package com.banking.account.infrastructure.persistence.specification;

import com.banking.account.application.dto.AccountFilter;
import com.banking.account.infrastructure.persistence.entity.AccountJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountSpecification {

    public static Specification<AccountJpaEntity> withFilter(AccountFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(filter.getAccountNumber()) && !filter.getAccountNumber().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                root.get("number"),
                                "%" + filter.getAccountNumber() + "%"
                        )
                );
            }

            if (Objects.nonNull(filter.getAccountType())) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("accountType"),
                                filter.getAccountType()
                        )
                );
            }

            if (Objects.nonNull(filter.getStatus())) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                filter.getStatus()
                        )
                );
            }

            if (Objects.nonNull(filter.getCustomerId())) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("customerId"),
                                filter.getCustomerId()
                        )
                );
            }

            if (Objects.nonNull(filter.getMinBalance())) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("currentBalance"),
                                filter.getMinBalance()
                        )
                );
            }

            if (Objects.nonNull(filter.getMaxBalance())) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("currentBalance"),
                                filter.getMaxBalance()
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}