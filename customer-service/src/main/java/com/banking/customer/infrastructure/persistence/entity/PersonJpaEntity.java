package com.banking.customer.infrastructure.persistence.entity;

import com.banking.customer.domain.model.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "persons", schema = "core")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PersonJpaEntity extends AbstractAuditableEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "identification", unique = true, nullable = false, length = 20)
    private String identification;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

}