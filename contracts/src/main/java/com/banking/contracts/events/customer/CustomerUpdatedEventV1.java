package com.banking.contracts.events.customer;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerUpdatedEventV1 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID customerId;
    private String customerIdValue;
    private String name;
    private String lastName;
    private String address;
    private String phone;
    private LocalDateTime occurredAt;

}