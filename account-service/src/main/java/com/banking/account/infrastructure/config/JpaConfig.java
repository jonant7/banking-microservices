package com.banking.account.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.banking.account.infrastructure.persistence.repository")
@EnableTransactionManagement
public class JpaConfig {
}