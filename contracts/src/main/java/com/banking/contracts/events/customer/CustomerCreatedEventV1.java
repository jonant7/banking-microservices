package com.banking.contracts.events.customer;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerCreatedEventV1 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID customerId;
    private String customerIdValue;
    private String name;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private String identification;
    private String address;
    private String phone;
    private String status;
    private LocalDateTime occurredAt;

}