package com.trippick.trippick_api_gateway.gateway.filter;

import org.springframework.cloud.gateway.server.mvc.filter.SimpleFilterSupplier;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Cloud Gateway의 MVC 기반 필터 기능을 위한 공급자(Supplier) 클래스입니다.
 * {@link Configuration} 어노테이션을 통해 Spring의 설정 컴포넌트로 등록됩니다.
 * 이 클래스는 {@link SimpleFilterSupplier}를 확장하여 사용자 정의 필터 함수들을
 * 게이트웨이 설정에서 사용할 수 있도록 제공합니다.
 */
@Configuration
public class GatewayFilterSupplier extends SimpleFilterSupplier {
    /**
     * {@code GatewayFilterSupplier}의 생성자입니다.
     * {@link GatewayFilterFunctions} 클래스에 정의된 정적 필터 함수들을 등록합니다.
     * 이렇게 등록된 필터 함수들은 Spring Cloud Gateway의 라우팅 규칙에서 이름으로 참조하여 사용할 수 있게 됩니다.
     * (예: YAML 설정에서 `filters: [AddAuthenticationHeader]`)
     */
    public GatewayFilterSupplier() {
        super(GatewayFilterFunctions.class);
    }
}
