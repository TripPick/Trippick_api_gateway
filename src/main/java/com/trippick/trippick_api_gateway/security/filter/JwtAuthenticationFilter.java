package com.trippick.trippick_api_gateway.security.filter;

import com.trippick.trippick_api_gateway.security.jwt.JwtTokenValidator;
import com.trippick.trippick_api_gateway.security.jwt.authentication.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 들어오는 모든 HTTP 요청을 가로채 JWT(JSON Web Token)를 확인하고 인증을 처리하는 Spring Security 필터입니다.
 * {@link OncePerRequestFilter}를 상속받아 각 요청에 대해 한 번만 실행되도록 보장합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // JWT 토큰의 유효성을 검증하는 컴포넌트입니다.
    private final JwtTokenValidator jwtTokenValidator;

    /**
     * 실제 필터링 로직을 수행하는 메소드입니다.
     * HTTP 요청에서 JWT를 추출하고, 유효성을 검증한 후,
     * 유효한 경우 Spring Security의 {@link SecurityContextHolder}에 인증 정보를 설정합니다.
     *
     * @param request     현재 HTTP 요청 객체
     * @param response    현재 HTTP 응답 객체
     * @param filterChain 다음 필터로 요청을 전달하거나, 필터 체인의 끝이면 대상 서블릿/컨트롤러로 전달하는 객체
     * @throws ServletException 서블릿 처리 중 예외 발생 시
     * @throws IOException      입출력 예외 발생 시
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 JWT 토큰을 추출합니다. (일반적으로 "Authorization" 헤더에서 "Bearer " 접두사로 시작하는 토큰)
        String jwtToken = jwtTokenValidator.getToken(request);

        if (jwtToken != null) { // 토큰이 존재하는 경우
            // 추출된 JWT 토큰의 유효성을 검증하고, 유효하다면 JwtAuthentication 객체를 반환받습니다.
            JwtAuthentication authentication = jwtTokenValidator.validateToken(jwtToken);
            if (authentication != null) { // 토큰이 유효하여 Authentication 객체가 생성된 경우
                // Spring Security의 SecurityContext에 인증 정보를 설정합니다.
                // 이렇게 설정된 인증 정보는 현재 요청 처리 과정 동안 유지됩니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 다음 필터로 요청과 응답을 전달합니다.
        // 만약 이 필터가 체인의 마지막 필터라면, 요청은 실제 요청을 처리할 서블릿이나 컨트롤러로 전달됩니다.
        filterChain.doFilter(request, response);
    }
}
