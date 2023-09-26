package com.benaya.tracing.kafkasb3simple.controller;

import com.benaya.tracing.kafkasb3simple.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Sb3ToSb2KafkaController {

    private final MessageService messageService;

    @GetMapping("/sb3tosb2Get")
    public String sb3ToSb2() throws ExecutionException, InterruptedException {
        CompletableFuture<SendResult<String, String>> messageResult = messageService.sendMessage("from sb3 to sb2");
        messageResult.get().getProducerRecord().headers().forEach(header -> log.info("header: key: {}, and value: {}", header.key(), new String(header.value(), StandardCharsets.UTF_8)));
        return messageResult.get().toString();
    }
    @PutMapping("/sb3tosb2Kafka")
    public void sb2ToSb3Kafka() {
        messageService.sendMessage("from sb3 to sb2");
    }
}
