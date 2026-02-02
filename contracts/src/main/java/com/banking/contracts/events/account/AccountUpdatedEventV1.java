package com.banking.contracts.events.account;

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
public class AccountUpdatedEventV1 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID accountId;
    private String accountNumber;
    private UUID customerId;
    private String status;
    private LocalDateTime occurredAt;

}