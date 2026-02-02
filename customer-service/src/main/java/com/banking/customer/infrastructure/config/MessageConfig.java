package com.banking.customer.infrastructure.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/messages", "classpath:i18n/validation");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }

}