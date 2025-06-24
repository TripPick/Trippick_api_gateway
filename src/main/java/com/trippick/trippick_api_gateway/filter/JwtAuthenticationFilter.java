package com.trippick.trippick_api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                try {
                    // JWT 토큰 검증 로직 (실제 구현에서는 JWT 라이브러리 사용)
                    if (isValidToken(token)) {
                        return chain.filter(exchange);
                    } else {
                        return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
                    }
                } catch (Exception e) {
                    return onError(exchange, "JWT token validation failed", HttpStatus.UNAUTHORIZED);
                }
            }
            
            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token) {
        // TODO: 실제 JWT 검증 로직 구현
        // 현재는 간단한 검증만 수행
        return token != null && !token.isEmpty();
    }

    private org.springframework.web.server.ServerWebExchange onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange;
    }

    public static class Config {
        // 설정 속성들
    }
} 