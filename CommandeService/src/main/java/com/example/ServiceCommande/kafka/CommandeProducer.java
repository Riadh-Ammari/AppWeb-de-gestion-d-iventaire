package com.example.ServiceCommande.kafka;



import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import primitives.OrderEvent;

@Service

public class CommandeProducer {
    private static final Logger LOGGER =LoggerFactory.getLogger(CommandeProducer.class);
    private NewTopic topic;

    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public CommandeProducer(NewTopic topic,KafkaTemplate<String,OrderEvent> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
        this.topic=topic;
    }
    public void envoyerMessage(OrderEvent event){
        LOGGER.info(String.format("order event => %s", event.toString()));
        Message<OrderEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }
}
