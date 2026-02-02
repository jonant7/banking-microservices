package com.banking.customer.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {

    public static final String CUSTOMER_EXCHANGE = "customer.exchange";
    public static final String CUSTOMER_CREATED_ROUTING_KEY = "customer.created";
    public static final String CUSTOMER_UPDATED_ROUTING_KEY = "customer.updated";

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_INTERVAL = 1000L;
    private static final double RETRY_MULTIPLIER = 2.0;
    private static final long MAX_RETRY_INTERVAL = 10000L;
    private static final int PREFETCH_COUNT = 10;

    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange(CUSTOMER_EXCHANGE, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        factory.setMessageConverter(jsonMessageConverter());
        factory.setPrefetchCount(PREFETCH_COUNT);
        factory.setRetryTemplate(retryTemplate());
        factory.setDefaultRequeueRejected(false);

        return factory;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(INITIAL_RETRY_INTERVAL);
        backOffPolicy.setMultiplier(RETRY_MULTIPLIER);
        backOffPolicy.setMaxInterval(MAX_RETRY_INTERVAL);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_RETRY_ATTEMPTS);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}