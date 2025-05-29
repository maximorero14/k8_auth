package com.maximorero.k8_auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	private static final Logger log = LoggerFactory.getLogger(HealthCheckController.class);


	@GetMapping("/ping")
	public ResponseEntity<JsonNode> ping() throws Exception {
		log.info("[log_name: ping]");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode response = mapper.createObjectNode().put("message", "pong_k8_auth");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}



	@GetMapping("/exception")
	public ResponseEntity<JsonNode> exception(@RequestParam(name = "throw", defaultValue = "false") boolean throwException) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		JsonNode response = mapper.createObjectNode().put("exception", "No se lanzó ninguna excepción.");

		if (throwException) {
			throw new RuntimeException("Se lanzó una excepción debido a throwException = true");
		}

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/health")
	public ResponseEntity<String> health() {
		log.info("[log_name: health]");
		return new ResponseEntity<>("ok_k8_auth_v2", HttpStatus.OK);
	}
}