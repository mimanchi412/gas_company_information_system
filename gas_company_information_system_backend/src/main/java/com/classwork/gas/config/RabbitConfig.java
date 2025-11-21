package com.classwork.gas.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue emailQueue(@Value("${email.queue:email-codes}") String queueName) {
        return new Queue(queueName, true);
    }
}
