package com.trippick.trippick_api_gateway.security.jwt.authentication;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * JWT(JSON Web Token)를 사용하여 인증된 사용자의 정보를 나타내는 Spring Security의 {@link org.springframework.security.core.Authentication} 구현체입니다.
 * 이 객체는 JWT가 성공적으로 검증된 후에 생성되며, 인증된 사용자의 주체(principal), JWT 토큰 문자열, 그리고 부여된 권한들을 포함합니다.
 */
@Getter
public class JwtAuthentication extends AbstractAuthenticationToken {
    // 인증에 사용된 JWT 토큰 문자열입니다.
    private final String token;
    // 인증된 사용자의 주체를 나타내는 {@link UserPrincipal} 객체입니다.
    private final UserPrincipal principal;

    /**
     * {@code JwtAuthentication} 객체를 생성합니다.
     * 이 생성자는 인증이 성공적으로 완료되었음을 가정하고, {@code setAuthenticated(true)}를 호출합니다.
     *
     * @param principal   인증된 사용자의 주체 ({@link UserPrincipal})
     * @param token       인증에 사용된 JWT 토큰 문자열
     * @param authorities 사용자에게 부여된 권한 컬렉션 ({@link GrantedAuthority})
     */
    public JwtAuthentication(UserPrincipal principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        // 인증 주체에 대한 추가 세부 정보를 설정합니다. 여기서는 principal 객체 자체를 사용합니다.
        this.setDetails(principal);
        // 이 토큰이 인증되었음을 명시적으로 설정합니다.
        setAuthenticated(true);
    }

    /**
     * 이 토큰이 인증되었는지 여부를 반환합니다.
     * {@code JwtAuthentication} 객체는 항상 인증된 상태로 생성되므로, 항상 {@code true}를 반환합니다.
     *
     * @return 항상 {@code true}
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    /**
     * 인증에 사용된 자격 증명(credentials)을 반환합니다.
     * JWT 인증의 경우, 이는 JWT 토큰 문자열 자체입니다.
     *
     * @return JWT 토큰 문자열
     */
    @Override
    public String getCredentials() {
        return token;
    }

    /**
     * 인증된 사용자의 주체(principal)를 반환합니다.
     *
     * @return {@link UserPrincipal} 객체
     */
    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }
}
