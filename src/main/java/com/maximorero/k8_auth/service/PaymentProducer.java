package com.maximorero.k8_auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maximorero.k8_auth.dto.PaymentRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${payment.exchange.name}")
    private String exchangeName;

    @Value("${payment.routing.key}")
    private String routingKey;

    public void sendPaymentRequest(PaymentRequest paymentRequest) {
        try {
            log.info("Sending payment request: {}", paymentRequest);
            rabbitTemplate.convertAndSend(exchangeName, routingKey, paymentRequest);
            log.info("Payment request sent successfully");
        } catch (Exception e) {
            log.error("Error sending payment request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send payment request", e);
        }
    }
}