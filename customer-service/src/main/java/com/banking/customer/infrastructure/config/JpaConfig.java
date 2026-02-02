package com.banking.customer.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.banking.customer.infrastructure.persistence.repository")
@EnableTransactionManagement
public class JpaConfig {
}