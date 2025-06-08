package com.maximorero.k8_auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maximorero.k8_auth.dto.PaymentRequest;

import lombok.RequiredArgsConstructor;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    UtilsService utilsService;

    public void publish(PaymentRequest paymentRequest) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            String json = objectMapper.writeValueAsString(paymentRequest);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get(); // Wait for the message to be sent

            // Log detailed information about the published message
            log.info("Message published to Kafka topic: {}", topic);
            log.info("Partition: {}, Offset: {}, Timestamp: {}", metadata.partition(), metadata.offset(), metadata.timestamp());
            log.debug("Published message content: {}", json);
        } catch (Exception e) {
            log.error("Error publishing to Kafka: {}", utilsService.getStackTraceAsString(e));
            throw new IllegalStateException("Error publishing to Kafka", e);
        }
    }
}
