package com.example.ServiceCommande.kafka;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.config.TopicBuilder;

import javax.annotation.PostConstruct;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Bean
    public NewTopic topic(){
        return TopicBuilder.name(topicName)
                .partitions(3) // Tu peux adapter selon ton besoin
                .replicas(1)
                .build();
    }
    @PostConstruct
    public void logTopicCreation() {
        System.out.println("✅ Kafka Topic initialisé : " + topicName);
    }
}
