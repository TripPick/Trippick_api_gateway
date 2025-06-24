package com.trippick.trippick_api_gateway.config;

import com.trippick.trippick_api_gateway.security.filter.JwtAuthenticationFilter;
import com.trippick.trippick_api_gateway.security.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정을 담당하는 클래스입니다.
 * {@link EnableWebSecurity} 어노테이션을 통해 Spring Security를 활성화하고,
 * 웹 애플리케이션의 보안 규칙을 정의합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    // JWT 토큰의 유효성을 검증하는 컴포넌트입니다.
    // 이 컴포넌트는 {@link JwtAuthenticationFilter}에서 사용됩니다.
    private final JwtTokenValidator jwtTokenValidator;

    /**
     * Spring Security의 필터 체인을 정의하는 Bean입니다.
     * HTTP 요청에 대한 보안 처리를 설정합니다.
     * @param http {@link HttpSecurity} 객체로, 보안 설정을 구성하는 데 사용됩니다.
     * @return 구성된 {@link SecurityFilterChain} 객체
     * @throws Exception 설정 과정에서 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
        http
                // CORS(Cross-Origin Resource Sharing) 설정을 적용합니다.
                // corsConfigurationSource() 메소드에서 정의한 설정을 사용합니다.
                .cors(httpSecurityCorsConfigurer -> {
                    httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
                })
                // CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화합니다.
                // REST API는 일반적으로 세션을 사용하지 않으므로 CSRF 공격에 덜 취약하며,
                // JWT와 같은 토큰 기반 인증을 사용할 경우 비활성화하는 경우가 많습니다.
                .csrf(AbstractHttpConfigurer::disable)
                // 현재 설정을 모든 경로("/**")에 적용합니다.
                .securityMatcher("/**")
                // 세션 관리를 STATELESS로 설정하여, 서버가 세션을 생성하거나 사용하지 않도록 합니다.
                // 이는 JWT와 같이 각 요청이 독립적으로 인증되는 방식에 적합합니다.
                .sessionManagement(sessionManagementConfigurer
                        -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 기본 폼 로그인 기능을 비활성화합니다. JWT 인증을 사용하므로 필요하지 않습니다.
                .formLogin(AbstractHttpConfigurer::disable)
                // HTTP Basic 인증 기능을 비활성화합니다. JWT 인증을 사용하므로 필요하지 않습니다.
                .httpBasic(AbstractHttpConfigurer::disable)
                // 커스텀 JWT 인증 필터인 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가합니다.
                // 이를 통해 요청이 UsernamePasswordAuthenticationFilter에 도달하기 전에 JWT 인증을 먼저 시도합니다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenValidator), UsernamePasswordAuthenticationFilter.class)
                // HTTP 요청에 대한 접근 권한을 설정합니다.
                .authorizeHttpRequests(registry -> registry
                        // "/api/user/v1/auth/**" 패턴의 경로는 인증 없이 모두 접근을 허용합니다. (예: 로그인, 회원가입 API)
                        .requestMatchers("/api/user/v1/auth/**").permitAll()
                        // 그 외의 모든 요청은 인증된 사용자만 접근을 허용합니다.
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 위한 {@link CorsConfigurationSource} Bean을 생성합니다.
     * 다른 도메인에서의 요청을 허용하기 위한 규칙을 정의합니다.
     * @return {@link CorsConfigurationSource} 객체
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 요청에 자격 증명(쿠키, HTTP 인증 등)을 포함하도록 허용합니다.
        config.setAllowCredentials(true);
        // 허용할 Origin 패턴을 설정합니다. "*"는 모든 Origin을 허용합니다.
        // 보안을 위해 실제 운영 환경에서는 구체적인 도메인 목록을 사용하는 것이 좋습니다.
        // config.setAllowedOrigins(List.of("*")); // 특정 Origin만 허용할 경우 사용
        config.setAllowedOriginPatterns(List.of("*"));
        // 허용할 HTTP 메소드를 설정합니다.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 모든 헤더를 허용합니다.
        config.setAllowedHeaders(List.of("*"));
        // 클라이언트가 접근할 수 있도록 노출할 헤더를 설정합니다. (예: 커스텀 헤더)
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위에서 정의한 CORS 설정을 등록합니다.
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
