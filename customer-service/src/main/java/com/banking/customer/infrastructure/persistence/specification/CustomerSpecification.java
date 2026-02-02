package com.banking.customer.infrastructure.persistence.specification;

import com.banking.customer.application.dto.CustomerFilter;
import com.banking.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerSpecification {

    public static Specification<CustomerJpaEntity> withFilter(CustomerFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(filter.getName()) && !filter.getName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%"
                        )
                );
            }

            if (Objects.nonNull(filter.getLastName()) && !filter.getLastName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")),
                                "%" + filter.getLastName().toLowerCase() + "%"
                        )
                );
            }

            if (Objects.nonNull(filter.getIdentification()) && !filter.getIdentification().isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("identification"),
                                filter.getIdentification()
                        )
                );
            }

            if (Objects.nonNull(filter.getCustomerId()) && !filter.getCustomerId().isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("customerId"),
                                filter.getCustomerId()
                        )
                );
            }

            if (Objects.nonNull(filter.getGender())) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("gender"),
                                filter.getGender()
                        )
                );
            }

            if (Objects.nonNull(filter.getBirthDateFrom())) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("birthDate"),
                                filter.getBirthDateFrom()
                        )
                );
            }

            if (Objects.nonNull(filter.getBirthDateTo())) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("birthDate"),
                                filter.getBirthDateTo()
                        )
                );
            }

            if (Objects.nonNull(filter.getMinAge())) {
                LocalDate maxBirthDate = LocalDate.now().minusYears(filter.getMinAge());
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("birthDate"),
                                maxBirthDate
                        )
                );
            }

            if (Objects.nonNull(filter.getMaxAge())) {
                LocalDate minBirthDate = LocalDate.now().minusYears(filter.getMaxAge() + 1).plusDays(1);
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("birthDate"),
                                minBirthDate
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

            if (Objects.nonNull(filter.getAddress()) && !filter.getAddress().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("address")),
                                "%" + filter.getAddress().toLowerCase() + "%"
                        )
                );
            }

            if (Objects.nonNull(filter.getPhone()) && !filter.getPhone().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                root.get("phone"),
                                "%" + filter.getPhone() + "%"
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}