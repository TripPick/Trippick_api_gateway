package com.trippick.trippick_api_gateway.common.context;

import com.trippick.trippick_api_gateway.common.exception.NotFound;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * API 게이트웨이를 통해 전달된 요청(Request) 헤더에서 특정 값들을 추출하는 유틸리티 클래스입니다.
 * Spring의 {@link RequestContextHolder}를 사용하여 현재 HTTP 요청 컨텍스트에 접근합니다.
 * 주로 마이크로서비스 아키텍처에서 API 게이트웨이가 백엔드 서비스로 전달하는
 * 사용자 인증 정보(예: 사용자 ID)나 클라이언트 관련 정보(예: 디바이스 종류, IP 주소)를 조회할 때 사용됩니다.
 */
public class GatewayRequestHeaderUtils {
    /**
     * 현재 HTTP 요청의 헤더에서 지정된 키(key)에 해당하는 값을 문자열로 반환합니다.
     * {@link RequestContextHolder#currentRequestAttributes()}를 통해 현재 요청의 속성을 가져오고,
     * 이를 {@link ServletRequestAttributes}로 캐스팅하여 {@code HttpServletRequest} 객체를 얻은 후,
     * 해당 요청 객체의 {@code getHeader(key)} 메서드를 호출하여 헤더 값을 조회합니다.
     *
     * @param key 조회하고자 하는 HTTP 헤더의 이름 (예: "X-Auth-UserId")
     * @return 요청 헤더에서 해당 {@code key}로 조회된 값. 만약 해당 {@code key}의 헤더가 없거나,
     *         현재 요청 컨텍스트를 사용할 수 없는 경우 {@code null}이 반환될 수 있습니다.
     *         (주의: {@code RequestContextHolder.currentRequestAttributes()}가 null을 반환하거나,
     *         {@code requestAttributes.getRequest()}가 null을 반환하는 극단적인 경우 NullPointerException 발생 가능성이 있습니다.
     *         실제 운영 환경에서는 이러한 경우에 대한 방어 코드가 필요할 수 있습니다.)
     */
    public static String getRequestHeaderParamAsString(String key) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return requestAttributes.getRequest().getHeader(key);
    }

    /**
     * 게이트웨이 요청 헤더에서 사용자 ID ("X-Auth-UserId") 값을 조회합니다.
     * 내부적으로 {@link #getRequestHeaderParamAsString(String)} 메서드를 사용합니다.
     *
     * @return "X-Auth-UserId" 헤더 값. 해당 헤더가 없으면 {@code null}을 반환합니다.
     */
    public static String getUserId() {
        String userId = getRequestHeaderParamAsString("X-Auth-UserId");
        if (userId == null) {
            return null;
        }

        return userId;
    }

    /**
     * 게이트웨이 요청 헤더에서 클라이언트 디바이스 정보 ("X-Client-Device") 값을 조회합니다.
     * 내부적으로 {@link #getRequestHeaderParamAsString(String)} 메서드를 사용합니다.
     *
     * @return "X-Client-Device" 헤더 값. 해당 헤더가 없으면 {@code null}을 반환합니다.
     */
    public static String getClientDevice() {
        String clientDevice = getRequestHeaderParamAsString("X-Client-Device");
        if (clientDevice == null) {
            return null;
        }

        return clientDevice;
    }

    /**
     * 게이트웨이 요청 헤더에서 클라이언트 IP 주소 ("X-Client-Address") 값을 조회합니다.
     * 내부적으로 {@link #getRequestHeaderParamAsString(String)} 메서드를 사용합니다.
     *
     * @return "X-Client-Address" 헤더 값. 해당 헤더가 없으면 {@code null}을 반환합니다.
     */
    public static String getClientAddress() {
        String clientAddress = getRequestHeaderParamAsString("X-Client-Address");
        if (clientAddress == null) {
            return null;
        }

        return clientAddress;
    }

    /**
     * 게이트웨이 요청 헤더에서 사용자 ID ("X-Auth-UserId") 값을 조회합니다.
     * 만약 해당 헤더 값이 없으면 {@link NotFound} 예외를 발생시킵니다.
     *
     * @return "X-Auth-UserId" 헤더 값. (null이 아님을 보장)
     * @throws NotFound "X-Auth-UserId" 헤더가 요청에 존재하지 않을 경우 발생합니다.
     */
    public static String getUserIdOrThrowException() {
        String userId = getUserId(); // 사용자 ID 조회
        if (userId == null) {
            throw new NotFound("헤더에 userId 정보가 없습니다."); // 없으면 예외 발생
        }

        return userId;
    }

    /**
     * 게이트웨이 요청 헤더에서 클라이언트 디바이스 정보 ("X-Client-Device") 값을 조회합니다.
     * 만약 해당 헤더 값이 없으면 {@link NotFound} 예외를 발생시킵니다.
     *
     * @return "X-Client-Device" 헤더 값. (null이 아님을 보장)
     * @throws NotFound "X-Client-Device" 헤더가 요청에 존재하지 않을 경우 발생합니다.
     */
    public static String getClientDeviceOrThrowException() {
        String clientDevice = getClientDevice();
        if (clientDevice == null) {
            throw new NotFound("헤더에 사용자 디바이스 정보가 없습니다.");
        }

        return clientDevice;
    }

    /**
     * 게이트웨이 요청 헤더에서 클라이언트 IP 주소 ("X-Client-Address") 값을 조회합니다.
     * 만약 해당 헤더 값이 없으면 {@link NotFound} 예외를 발생시킵니다.
     *
     * @return "X-Client-Address" 헤더 값. (null이 아님을 보장)
     * @throws NotFound "X-Client-Address" 헤더가 요청에 존재하지 않을 경우 발생합니다.
     */
    public static String getClientAddressOrThrowException() {
        String clientAddress = getClientAddress();
        if (clientAddress == null) {
            throw new NotFound("헤더에 사용자IP 주소 정보가 없습니다.");
        }

        return clientAddress;
    }
}