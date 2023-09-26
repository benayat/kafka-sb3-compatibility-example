package com.benaya.tracing.kafkasb3simple.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<SendResult<String,String>> sendMessage(String msg) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("sb2InputTopic", msg);
        CompletableFuture<SendResult<String, String>> sendResultCompletableFuture = kafkaTemplate.send(producerRecord);
        sendResultCompletableFuture.whenComplete((result, ex) -> {
            if (Objects.nonNull(ex)) {
                log.error("Unable to deliver message [{}]. {}", msg, ex.getMessage());
            } else {
                assert result != null;
                log.info("Message [{}] delivered with metadata {}", msg, result.getRecordMetadata());
                log.info("in sender method, headers are: {}",result.getProducerRecord().headers().toString());
            }
        });

        return sendResultCompletableFuture;
    }

    @KafkaListener(topics = {"sb3InputTopic"}, groupId = "group1", autoStartup = "true")
    public void listen(@Payload ConsumerRecord<String, String> consumerRecord, @Headers MessageHeaders messageHeaders ) {

        log.info("Received Message in sb3 with payload: {}", consumerRecord.value());
        messageHeaders.forEach((key, value) -> log.info("from headers param - header: key: {}, and value: {}", key, value));
        consumerRecord.headers().forEach(header -> log.info("from consumer-record - header: key: {}, and value: {}", header.key(), new String(header.value())));
    }

}
