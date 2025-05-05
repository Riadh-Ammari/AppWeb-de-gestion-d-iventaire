package com.example.FournisseurService.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import primitives.FournisseurEvent;

@Service
public class FournisseurProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FournisseurProducer.class);
    private NewTopic topic;

    private KafkaTemplate<String, FournisseurEvent> kafkaTemplate;

    public FournisseurProducer(NewTopic topic,KafkaTemplate<String,FournisseurEvent> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
        this.topic=topic;
    }
    public void envoyerMessage(FournisseurEvent event){
        LOGGER.info(String.format("order event => %s", event.toString()));
        Message<FournisseurEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }
}
