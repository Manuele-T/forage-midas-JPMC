package com.jpmc.midascore;

import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;

@TestConfiguration
public class KafkaBrokerCustomizer implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof EmbeddedKafkaKraftBroker broker) {
            broker.brokerProperties(Map.of(
                "process.roles",             "broker,controller",
                "node.id",                   "1",
                "broker.id",                 "1",               // match node.id
                "controller.quorum.voters",  "1@localhost:0"
            ));
        }
        return bean;
    }
}
