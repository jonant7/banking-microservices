package com.banking.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CustomerServiceApplicationTests extends IntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void postgresContainerIsRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void rabbitMQContainerIsRunning() {
        assertThat(rabbitmq.isRunning()).isTrue();
    }

}