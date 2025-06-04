package com.maximorero.k8_auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maximorero.k8_auth.dto.PaymentRequest;
import com.maximorero.k8_auth.dto.PaymentResponse;
import com.maximorero.k8_auth.rest_client.EnhancedRestClient;
import com.maximorero.k8_auth.rest_client.RestClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

	@Autowired
	private EnhancedRestClient restClient;

	@Value("${services.payment.url}")
	private String paymentServiceUrl;

	@PostMapping("/create")
	public ResponseEntity<?> createPayment(@RequestBody PaymentRequest payment) {
		String metricName = "create_payment";

		try {
			RestClientResponse<PaymentResponse> response = restClient.post(
					metricName,
					paymentServiceUrl+"/payment/create",
					payment,
					Map.of("Content-Type", "application/json"),
					PaymentResponse.class
			);

			if (!(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError())) {
				return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
			} else {
				return handleErrorResponse(response);
			}

		} catch (Exception e) {
			log.error("Unexpected error in createPayment: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Internal server error", "message", "An unexpected error occurred"));
		}
	}

	private ResponseEntity<?> handleErrorResponse(RestClientResponse<PaymentResponse> response) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("success", false);
		errorResponse.put("errorType", response.getErrorType());
		errorResponse.put("statusCode", response.getStatusCode().value());

		if (response.isConnectionError()) {
			errorResponse.put("error", "Service unavailable");
			errorResponse.put("message", "Payment service is currently unavailable");
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);

		} else if (response.isParsingError()) {
			errorResponse.put("error", "Response parsing error");
			errorResponse.put("message", "Received unexpected response format");
			errorResponse.put("rawResponse", response.getRawBody());
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);

		} else if (response.isClientError()) {
			// Forward client errors (4xx) as-is
			if (response.getBody() != null) {
				return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
			} else if (response.getRawBody() != null) {
				errorResponse.put("message", response.getRawBody());
				return ResponseEntity.status(response.getStatusCode()).body(errorResponse);
			}
			return ResponseEntity.status(response.getStatusCode()).body(errorResponse);

		} else if (response.isServerError()) {
			errorResponse.put("error", "External service error");
			errorResponse.put("message", "Payment service encountered an error");
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);

		} else {
			errorResponse.put("error", "Unknown error");
			errorResponse.put("message", "An unknown error occurred");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}