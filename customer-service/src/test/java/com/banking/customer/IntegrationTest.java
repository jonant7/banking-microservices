package com.banking.customer;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17.6")
                    .withDatabaseName("account_db_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    @Container
    @ServiceConnection
    protected static final RabbitMQContainer rabbitmq =
            new RabbitMQContainer("rabbitmq:3.13-management")
                    .withReuse(true);

}