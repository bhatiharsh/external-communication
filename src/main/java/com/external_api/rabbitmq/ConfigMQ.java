package com.external_api.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConfigMQ {

    public static final String MAIL_EXCHANGE = "mail.exchange";
    public static final String MAIL_QUEUE = "mail.queue";
    public static final String MAIL_ROUTING_KEY = "mail.send";
    public static final String MAIL_DLX = "mail.dlx";
    public static final String MAIL_DLQ = "mail.dlq";


    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(
                MAIL_EXCHANGE,
                true,
                false
        );
    }


    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(
                MAIL_DLX,
                true,
                false
        );
    }

    @Bean
    public Queue mailQueue() {

        return QueueBuilder.durable(MAIL_QUEUE)
                .deadLetterExchange(MAIL_DLX)
                .deadLetterRoutingKey(MAIL_QUEUE)
                .build();
    }


    @Bean
    public Queue mailDlq() {
        return QueueBuilder.durable(MAIL_DLQ).build();
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MAIL_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(mailDlq()).to(deadLetterExchange()).with(MAIL_QUEUE);
    }


    @Bean
    public SimpleRabbitListenerContainerFactory mailListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        configurer.configure(
                factory,
                connectionFactory
        );

        // Start with 3 consumers
        factory.setConcurrentConsumers(3);

        // Scale up to maximum 10 consumers
        factory.setMaxConcurrentConsumers(10);

        // Don't prefetch too many email messages
        factory.setPrefetchCount(3);

        // Failed message should not return endlessly
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
// Exchange
// Key
// Queue
// We will push the message to Exchange then using the Key it will send to queue


//