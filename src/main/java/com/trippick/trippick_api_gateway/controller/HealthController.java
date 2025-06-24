package com.trippick.trippick_api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "trippick-api-gateway");
        response.put("timestamp", System.currentTimeMillis());
        return Mono.just(response);
    }

    @GetMapping("/")
    public Mono<Map<String, String>> root() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "TripPick API Gateway is running");
        response.put("version", "1.0.0");
        return Mono.just(response);
    }
} 