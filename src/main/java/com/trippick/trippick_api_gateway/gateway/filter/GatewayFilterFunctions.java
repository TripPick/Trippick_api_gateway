package com.trippick.trippick_api_gateway.gateway.filter;


import org.springframework.cloud.gateway.server.mvc.common.Shortcut;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.HandlerFilterFunction.ofRequestProcessor;

/**
 * Spring Cloud Gateway에서 사용될 수 있는 공통 핸들러 필터 함수들을 정의하는 인터페이스입니다.
 * 이 인터페이스에 정의된 정적 메소드들은 특정 기능을 수행하는 {@link HandlerFilterFunction}을 반환합니다.
 * 이러한 함수들은 게이트웨이 라우팅 설정 시 요청 또는 응답을 가로채 추가적인 처리를 수행하는 데 사용될 수 있습니다.
 */
public interface GatewayFilterFunctions {
    /**
     * 요청에 인증 관련 헤더를 추가하는 {@link HandlerFilterFunction}을 반환합니다.
     * 이 함수는 {@link AuthenticationHeaderFilterFunction#addHeader()}를 사용하여 실제 헤더 추가 로직을 수행합니다.
     * {@link Shortcut} 어노테이션은 Spring Cloud Gateway의 YAML 설정 등에서 이 필터 함수를 간결하게 참조할 수 있도록 합니다.
     * (예: `filters: [AddAuthenticationHeader]`)
     * @return 요청을 처리하여 인증 헤더를 추가하는 {@link HandlerFilterFunction}
     */
    @Shortcut
    static HandlerFilterFunction<ServerResponse, ServerResponse> addAuthenticationHeader() {
        return ofRequestProcessor(AuthenticationHeaderFilterFunction.addHeader());
    }
}
