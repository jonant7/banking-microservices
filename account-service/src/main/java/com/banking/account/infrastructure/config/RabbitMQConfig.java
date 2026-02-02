package com.banking.account.infrastructure.config;

import org.springframework.amqp.core.*;
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

    public static final String ACCOUNT_EXCHANGE = "account.exchange";
    public static final String ACCOUNT_DLX_EXCHANGE = "account.dlx.exchange";

    public static final String ACCOUNT_CREATED_QUEUE = "account.created";
    public static final String ACCOUNT_UPDATED_QUEUE = "account.updated";
    public static final String TRANSACTION_CREATED_QUEUE = "transaction.created";

    public static final String ACCOUNT_CREATED_DLQ = "account.created.dlq";
    public static final String ACCOUNT_UPDATED_DLQ = "account.updated.dlq";
    public static final String TRANSACTION_CREATED_DLQ = "transaction.created.dlq";

    public static final String ACCOUNT_CREATED_ROUTING_KEY = "account.created";
    public static final String ACCOUNT_UPDATED_ROUTING_KEY = "account.updated";
    public static final String TRANSACTION_CREATED_ROUTING_KEY = "transaction.created";

    public static final String CUSTOMER_EXCHANGE = "customer.exchange";
    public static final String CUSTOMER_CREATED_QUEUE = "customer.created.account";
    public static final String CUSTOMER_UPDATED_QUEUE = "customer.updated.account";
    public static final String CUSTOMER_STATUS_CHANGED_QUEUE = "customer.status.changed.account";

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_INTERVAL = 1000L;
    private static final double RETRY_MULTIPLIER = 2.0;
    private static final long MAX_RETRY_INTERVAL = 10000L;
    private static final int PREFETCH_COUNT = 10;

    @Bean
    public TopicExchange accountExchange() {
        return new TopicExchange(ACCOUNT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange accountDlxExchange() {
        return new TopicExchange(ACCOUNT_DLX_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange customerExchange() {
        return new TopicExchange(CUSTOMER_EXCHANGE, true, false);
    }

    @Bean
    public Queue accountCreatedQueue() {
        return QueueBuilder.durable(ACCOUNT_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ACCOUNT_CREATED_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue accountUpdatedQueue() {
        return QueueBuilder.durable(ACCOUNT_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ACCOUNT_UPDATED_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue transactionCreatedQueue() {
        return QueueBuilder.durable(TRANSACTION_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRANSACTION_CREATED_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue accountCreatedDlq() {
        return QueueBuilder.durable(ACCOUNT_CREATED_DLQ).build();
    }

    @Bean
    public Queue accountUpdatedDlq() {
        return QueueBuilder.durable(ACCOUNT_UPDATED_DLQ).build();
    }

    @Bean
    public Queue transactionCreatedDlq() {
        return QueueBuilder.durable(TRANSACTION_CREATED_DLQ).build();
    }

    @Bean
    public Queue customerCreatedAccountQueue() {
        return QueueBuilder.durable(CUSTOMER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "customer.created.dlq")
                .build();
    }

    @Bean
    public Queue customerUpdatedAccountQueue() {
        return QueueBuilder.durable(CUSTOMER_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "customer.updated.dlq")
                .build();
    }

    @Bean
    public Queue customerStatusChangedAccountQueue() {
        return QueueBuilder.durable(CUSTOMER_STATUS_CHANGED_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "customer.status.changed.dlq")
                .build();
    }

    @Bean
    public Binding accountCreatedBinding() {
        return BindingBuilder
                .bind(accountCreatedQueue())
                .to(accountExchange())
                .with(ACCOUNT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding accountUpdatedBinding() {
        return BindingBuilder
                .bind(accountUpdatedQueue())
                .to(accountExchange())
                .with(ACCOUNT_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding transactionCreatedBinding() {
        return BindingBuilder
                .bind(transactionCreatedQueue())
                .to(accountExchange())
                .with(TRANSACTION_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding accountCreatedDlqBinding() {
        return BindingBuilder
                .bind(accountCreatedDlq())
                .to(accountDlxExchange())
                .with(ACCOUNT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding accountUpdatedDlqBinding() {
        return BindingBuilder
                .bind(accountUpdatedDlq())
                .to(accountDlxExchange())
                .with(ACCOUNT_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding transactionCreatedDlqBinding() {
        return BindingBuilder
                .bind(transactionCreatedDlq())
                .to(accountDlxExchange())
                .with(TRANSACTION_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding customerCreatedAccountBinding() {
        return BindingBuilder
                .bind(customerCreatedAccountQueue())
                .to(customerExchange())
                .with("customer.created");
    }

    @Bean
    public Binding customerUpdatedAccountBinding() {
        return BindingBuilder
                .bind(customerUpdatedAccountQueue())
                .to(customerExchange())
                .with("customer.updated");
    }

    @Bean
    public Binding customerStatusChangedAccountBinding() {
        return BindingBuilder
                .bind(customerStatusChangedAccountQueue())
                .to(customerExchange())
                .with("customer.updated.activated");
    }

    @Bean
    public Binding customerStatusChangedAccountBinding2() {
        return BindingBuilder
                .bind(customerStatusChangedAccountQueue())
                .to(customerExchange())
                .with("customer.updated.deactivated");
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