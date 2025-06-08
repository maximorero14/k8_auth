package com.maximorero.k8_auth.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.maximorero.k8_auth.dto.PaymentRequest;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaPublisher {

    @Autowired
    private KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    @Value("${kafka.payment.topic}")
    private String paymentsTopic;

    public CompletableFuture<SendResult<String, PaymentRequest>> publish(PaymentRequest paymentRequest) {
        log.info("Starting to send payment request to Kafka: {}", paymentRequest);
        log.info("Kafka topic: {}", paymentsTopic);

        return kafkaTemplate.send(paymentsTopic, paymentRequest)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("OKKK!!! Payment sent successfully: {} to partition {}",
                        paymentRequest, result.getRecordMetadata().partition());
                    log.info("Message metadata: offset={}, timestamp={}",
                        result.getRecordMetadata().offset(), result.getRecordMetadata().timestamp());
                } else {
                    log.error("Failed to send payment request: {}", paymentRequest, ex);
                }
            });
    }
}