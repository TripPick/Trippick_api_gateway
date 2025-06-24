package com.trippick.trippick_api_gateway.advice;

import com.trippick.trippick_api_gateway.advice.parameter.ParameterErrorDto;
import com.trippick.trippick_api_gateway.common.dto.ApiResponseDto;
import com.trippick.trippick_api_gateway.common.exception.BadParameter;
import com.trippick.trippick_api_gateway.common.exception.ClientError;
import com.trippick.trippick_api_gateway.common.exception.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * API 공통 예외 처리를 위한 Advice 클래스입니다.
 * {@link RestControllerAdvice}를 사용하여 전역적으로 예외를 핸들링합니다.
 */
@Slf4j
@Order(value = 1) // 여러 Advice 클래스가 있을 경우 우선순위를 지정합니다.
@RestControllerAdvice
public class ApiCommonAdvice {
    /**
     * 잘못된 파라미터 요청 시 발생하는 {@link BadParameter} 예외를 처리합니다.
     * HTTP 상태 코드 400 (Bad Request)을 반환합니다.
     *
     * @param e 발생한 BadParameter 예외 객체
     * @return 에러 코드와 메시지를 담은 {@link ApiResponseDto}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadParameter.class})
    public ApiResponseDto<String> handleBadParameter(BadParameter e) {
        return ApiResponseDto.createError(
                e.getErrorCode(),
                e.getErrorMessage()
        );
    }

    /**
     * 리소스를 찾을 수 없을 때 발생하는 {@link NotFound} 예외를 처리합니다.
     * HTTP 상태 코드 404 (Not Found)를 반환합니다.
     *
     * @param e 발생한 NotFound 예외 객체
     * @return 에러 코드와 메시지를 담은 {@link ApiResponseDto}
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFound.class})
    public ApiResponseDto<String> handleNotFound(NotFound e) {
        return ApiResponseDto.createError(
                e.getErrorCode(),
                e.getErrorMessage()
        );
    }

    /**
     * 클라이언트 측 에러 발생 시 {@link ClientError} 예외를 처리합니다.
     * HTTP 상태 코드 400 (Bad Request)을 반환합니다.
     *
     * @param e 발생한 ClientError 예외 객체
     * @return 에러 코드와 메시지를 담은 {@link ApiResponseDto}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ClientError.class})
    public ApiResponseDto<String> handleClientError(ClientError e) {
        return ApiResponseDto.createError(
                e.getErrorCode(),
                e.getErrorMessage()
        );
    }

    /**
     * Spring MVC에서 정적 리소스 등을 찾을 수 없을 때 발생하는 {@link NoResourceFoundException} 예외를 처리합니다.
     * HTTP 상태 코드 404 (Not Found)를 반환합니다.
     *
     * @param e 발생한 NoResourceFoundException 예외 객체
     * @return "NoResource" 에러 코드와 기본 메시지를 담은 {@link ApiResponseDto}
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoResourceFoundException.class})
    public ApiResponseDto<String> handleNoResourceFoundException(NoResourceFoundException e) {
        return ApiResponseDto.createError(
                "NoResource",
                "리소스를 찾을 수 없습니다.");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({InsufficientAuthenticationException.class})
    public ApiResponseDto<String> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException e) {
        return ApiResponseDto.createError(
                "Unauthenticated",
                "인증되지 않았습니다.");
    }

    /**
     * {@code @Valid} 어노테이션을 사용한 요청 파라미터 검증 실패 시 발생하는 {@link MethodArgumentNotValidException} 예외를 처리합니다.
     * HTTP 상태 코드 400 (Bad Request)을 반환합니다.
     *
     * @param e 발생한 MethodArgumentNotValidException 예외 객체
     * @return "ParameterNotValid" 에러 코드와 필드별 에러 메시지 목록을 담은 {@link ApiResponseDto}
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResponseDto<ParameterErrorDto.FieldList> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        ParameterErrorDto.FieldList fieldList = ParameterErrorDto.FieldList.of(result);

        String errorMessage = fieldList.getErrorMessage();
        return ApiResponseDto.createError("ParameterNotValid", errorMessage, fieldList);
    }

    /**
     * 위에서 처리되지 않은 모든 {@link Exception}을 처리하는 핸들러입니다.
     * HTTP 상태 코드 500 (Internal Server Error)을 반환합니다.
     *
     * @param e 발생한 Exception 객체
     * @return "ServerError" 에러 코드와 기본 서버 에러 메시지를 담은 {@link ApiResponseDto}
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ApiResponseDto<String > handleException(Exception e) {
        // 실제 운영 환경에서는 e.getMessage() 또는 스택 트레이스를 로깅하는 것이 중요합니다.
        log.error("Unhandled exception occurred: {}", e.getMessage(), e);
        return ApiResponseDto.createError(
                "ServerError",
                "서버 에러입니다.");
    }
}
