package com.maximorero.k8_auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maximorero.k8_auth.dto.PaymentRequest;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NatsPublisher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${nats.url}")
    private String natsUrl;

    @Value("${nats.subject}")
    private String subject;

    public void publish(PaymentRequest paymentRequest) {
        try (Connection natsConnection = Nats.connect(new Options.Builder().server(natsUrl).build())) {
            String json = objectMapper.writeValueAsString(paymentRequest);
            natsConnection.publish(subject, json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Error publishing to NATS", e);
        }
    }
}
