package com.trippick.trippick_api_gateway.common.exception;

public class BadParameter extends ClientError {
    public BadParameter(String message) {
        this.errorCode = "BadParameter";
        this.errorMessage = message;
    }
}
