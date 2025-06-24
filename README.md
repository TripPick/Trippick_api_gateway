# TripPick API Gateway

TripPick 프로젝트의 API Gateway 서비스입니다. 모든 클라이언트 요청을 받아서 적절한 마이크로서비스로 라우팅합니다.

## 주요 기능

- **라우팅**: 요청 경로에 따라 적절한 서비스로 요청을 전달
- **CORS 처리**: 크로스 오리진 요청 처리
- **보안**: JWT 토큰 검증 및 보안 설정
- **로깅**: 요청/응답 로깅
- **헬스 체크**: 서비스 상태 모니터링

## 라우팅 설정

### Member Service
- **경로**: `/api/member/**`
- **대상**: `http://localhost:8081`
- **설명**: 사용자 인증, 회원가입, 로그인 등

### OAuth Kakao Service
- **경로**: `/api/oauth/**`
- **대상**: `http://localhost:8085`
- **설명**: 카카오 OAuth 인증

### Frontend Service
- **경로**: `/**`
- **대상**: `http://localhost:5173`
- **설명**: React 프론트엔드 애플리케이션

## 실행 방법

### 로컬 개발 환경
```bash
# 프로젝트 루트 디렉토리에서
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 프로덕션 환경
```bash
./gradlew bootRun
```

## 포트 설정

- **API Gateway**: 8080
- **Member Service**: 8081
- **OAuth Kakao Service**: 8085
- **Frontend Service**: 5173

## 환경 변수

다음 환경 변수를 설정할 수 있습니다:

- `KAKAO_CLIENT_ID`: 카카오 OAuth 클라이언트 ID
- `KAKAO_CLIENT_SECRET`: 카카오 OAuth 클라이언트 시크릿
- `KAKAO_REDIRECT_URI`: 카카오 OAuth 리다이렉트 URI

## API 엔드포인트

### 헬스 체크
- `GET /health`: 서비스 상태 확인
- `GET /`: 루트 엔드포인트

### Actuator 엔드포인트
- `GET /actuator/health`: 상세 헬스 정보
- `GET /actuator/info`: 애플리케이션 정보
- `GET /actuator/gateway`: Gateway 라우트 정보

## 개발 가이드

### 새로운 라우트 추가
`application.yml` 또는 `application-local.yml`의 `spring.cloud.gateway.routes` 섹션에 새로운 라우트를 추가하세요.

### 필터 추가
`filter` 패키지에 새로운 필터를 생성하고 필요한 경우 `application.yml`에서 설정하세요.

## 문제 해결

### CORS 오류
- `CorsConfig.java`에서 허용된 오리진과 메서드를 확인하세요.
- 프론트엔드에서 올바른 포트로 요청을 보내고 있는지 확인하세요.

### 라우팅 오류
- 대상 서비스가 실행 중인지 확인하세요.
- 포트 번호가 올바른지 확인하세요.
- 로그를 확인하여 요청이 올바른 서비스로 전달되는지 확인하세요. 