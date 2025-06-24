package com.trippick.trippick_api_gateway.security.jwt;

// 필요한 클래스들을 임포트합니다.
import com.trippick.trippick_api_gateway.security.jwt.authentication.JwtAuthentication;
import com.trippick.trippick_api_gateway.security.jwt.authentication.UserPrincipal;
import com.trippick.trippick_api_gateway.security.jwt.props.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JWT 토큰을 검증하는 역할을 하는 Spring 컴포넌트입니다.
 * 토큰의 유효성(서명, 만료 시간 등)을 확인하고, 유효한 경우 사용자 정보와 권한을 추출하여
 * Spring Security 컨텍스트에서 사용될 {@link JwtAuthentication} 객체를 생성합니다.
 */
@Component
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
public class JwtTokenValidator {
    // JWT 관련 설정을 담고 있는 객체입니다. (예: 비밀키, 토큰 만료 시간 등)
    private final JwtConfigProperties configProperties;
    // JWT 서명 검증에 사용될 비밀키입니다.
    // volatile 키워드는 멀티스레드 환경에서 secretKey 변수의 가시성을 보장합니다.
    private volatile SecretKey secretKey;

    /**
     * JWT 서명 검증에 사용될 {@link SecretKey}를 반환합니다.
     * 이 메소드는 더블 체킹 락(double-checked locking) 패턴을 사용하여 스레드 안전하게 비밀키를 초기화합니다.
     * 비밀키는 {@link JwtConfigProperties}에 설정된 base64 인코딩된 문자열로부터 생성됩니다.
     * @return JWT 서명 검증용 {@link SecretKey}
     */
    private SecretKey getSecretKey() {
        if (secretKey == null) { // 첫 번째 null 체크 (동기화 없이)
            synchronized (this) { // 동기화 블록 진입
                if (secretKey == null) { // 두 번째 null 체크 (동기화 상태에서)
                    // 설정 파일에서 base64로 인코딩된 비밀키를 가져와 디코딩한 후, HMAC-SHA 알고리즘용 SecretKey 객체를 생성합니다.
                    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(configProperties.getSecretKey()));
                }
            }
        }
        return secretKey;
    }

    /**
     * 주어진 JWT 문자열을 검증합니다.
     * 토큰의 서명, 만료 시간, 특정 클레임(userId, tokenType)을 확인합니다.
     * 모든 검증을 통과하면, 사용자 정보와 권한을 담은 {@link JwtAuthentication} 객체를 반환합니다.
     * 검증에 실패하면 null을 반환합니다.
     *
     * @param token 검증할 JWT 문자열
     * @return 유효한 경우 {@link JwtAuthentication} 객체, 그렇지 않으면 null
     */
    public JwtAuthentication validateToken(String token) {
        String userId = null;
        // 토큰을 파싱하고 서명을 검증하여 클레임(토큰에 담긴 정보)을 가져옵니다.
        final Claims claims = this.verifyAndGetClaims(token);
        if (claims == null) { // 클레임이 null이면 (검증 실패 또는 오류) null 반환
            return null;
        }
        // 토큰의 만료 시간을 가져옵니다.
        Date expirationDate = claims.getExpiration();
        if (expirationDate == null || expirationDate.before(new Date())) { // 만료 시간이 없거나 현재 시간 이전이면 null 반환
            return null;
        }
        // 클레임에서 "userId"를 추출합니다.
        userId = claims.get("userId", String.class);
        // 클레임에서 "tokenType"을 추출합니다.
        String tokenType = claims.get("tokenType", String.class);
        if (!"access".equals(tokenType)) { // 토큰 타입이 "access"가 아니면 null 반환 (예: refresh 토큰 등 다른 타입일 수 있음)
            return null;
        }
        // 추출한 userId로 UserPrincipal 객체를 생성합니다. (Spring Security에서 사용될 사용자 정보)
        UserPrincipal principal = new UserPrincipal(userId);
        // 사용자에게 부여된 권한 목록을 가져옵니다. 여기서는 "user" 역할을 하드코딩하고 있습니다.
        // 실제 애플리케이션에서는 토큰의 클레임이나 DB 조회 등을 통해 동적으로 권한을 설정할 수 있습니다.
        return new JwtAuthentication(principal, token, getGrantedAuthorities("user"));
    }

    /**
     * JWT 문자열을 파싱하고 서명을 검증하여 클레임(payload)을 반환합니다.
     * JJWT 라이브러리를 사용합니다.
     * @param token 검증할 JWT 문자열
     * @return 파싱 및 검증에 성공하면 {@link Claims} 객체, 실패하면 null
     */
    private Claims verifyAndGetClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) { // 토큰 파싱/검증 중 예외 발생 시 (예: SignatureException, MalformedJwtException, ExpiredJwtException 등)
            claims = null;
        }
        return claims;
    }

    /**
     * 주어진 역할(role) 문자열을 기반으로 {@link GrantedAuthority} 리스트를 생성합니다.
     * Spring Security는 이 객체들을 사용하여 사용자의 권한을 나타냅니다.
     * @param role 사용자에게 부여할 역할 문자열
     * @return {@link GrantedAuthority}의 리스트
     */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (role != null) { // 역할이 null이 아니면
            // SimpleGrantedAuthority 객체를 생성하여 리스트에 추가합니다.
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        return grantedAuthorities;
    }

    /**
     * {@link HttpServletRequest}에서 JWT를 추출합니다.
     * 일반적으로 "Authorization" 헤더에서 "Bearer " 접두사로 시작하는 토큰을 찾습니다.
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 문자열, 없거나 형식이 맞지 않으면 null
     */
    public String getToken(HttpServletRequest request) {
        // 설정된 헤더 이름(예: "Authorization")으로 헤더 값을 가져옵니다.
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer ")) { // 헤더 값이 있고 "Bearer "로 시작하면
            // "Bearer " 접두사를 제외한 실제 토큰 문자열을 반환합니다.
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * {@link HttpServletRequest}에서 JWT가 담길 것으로 예상되는 HTTP 헤더의 값을 가져옵니다.
     * 헤더 이름은 {@link JwtConfigProperties}에서 가져옵니다.
     * @param request HTTP 요청 객체
     * @return 지정된 헤더의 값, 헤더가 없으면 null
     */
    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(configProperties.getHeader());
    }
}
