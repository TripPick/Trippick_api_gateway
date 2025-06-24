package com.trippick.trippick_api_gateway.gateway.filter;

import com.trippick.trippick_api_gateway.security.jwt.authentication.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.function.Function;

/**
 * Spring Cloud Gateway에서 사용될 수 있는 함수형 필터 로직을 제공하는 클래스입니다.
 * 이 클래스의 메소드는 요청에 특정 헤더를 추가하는 기능을 수행합니다.
 */
class AuthenticationHeaderFilterFunction {
    /**
     * 들어오는 {@link ServerRequest}에 인증 관련 헤더를 추가하는 {@link Function}을 반환합니다.
     * 이 함수는 Spring Security 컨텍스트에서 사용자 정보를 가져와 'X-Auth-UserId' 헤더를 추가하고,
     * 클라이언트 IP 주소와 장치 정보를 각각 'X-Client-Address', 'X-Client-Device' 헤더로 추가합니다.
     *
     * @return {@link ServerRequest}를 입력받아 헤더가 추가된 새로운 {@link ServerRequest}를 반환하는 함수
     */
    public static Function<ServerRequest, ServerRequest> addHeader() {
        return request -> {
            // 기존 요청으로부터 ServerRequest.Builder를 생성하여 요청을 수정할 수 있도록 합니다.
            ServerRequest.Builder requestBuilder = ServerRequest.from(request);
            // Spring Security의 SecurityContextHolder에서 현재 인증된 사용자의 Principal 객체를 가져옵니다.
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // Principal 객체가 UserPrincipal의 인스턴스인지 확인합니다.
            if (principal instanceof UserPrincipal userPrincipal) {
                // UserPrincipal에서 사용자 ID를 가져와 'X-Auth-UserId' 헤더에 추가합니다.
                requestBuilder.header("X-Auth-UserId", userPrincipal.getUserId());

                // 필요시 권한 정보 입력
                // 예: userPrincipal에서 권한 정보를 가져와 'X-Auth-Authorities' 헤더에 추가할 수 있습니다.
                // requestBuilder.header("X-Auth-Authorities", ...);
            }

            // 클라이언트의 원격 IP 주소를 가져옵니다. 현재는 하드코딩되어 있습니다.
            // String remoteAddr = HttpUtils.getRemoteAddr(requestBuildert.servletRequest());
            String remoteAddr = "70.1.23.15";
            // 'X-Client-Address' 헤더에 클라이언트 IP 주소를 추가합니다.
            requestBuilder.header("X-Client-Address", remoteAddr);

            // 클라이언트 장치 정보를 설정합니다. 현재는 "WEB"으로 하드코딩되어 있습니다.
            String device = "WEB";
            // 'X-Client-Device' 헤더에 클라이언트 장치 정보를 추가합니다.
            requestBuilder.header("X-Client-Device", device);

            // 수정된 헤더 정보를 포함하여 새로운 ServerRequest 객체를 빌드하여 반환합니다.
            return requestBuilder.build();
        };
    }
}
