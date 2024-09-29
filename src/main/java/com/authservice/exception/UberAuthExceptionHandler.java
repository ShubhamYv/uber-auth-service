package com.authservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.authservice.constants.ErrorCodeEnum;
import com.authservice.pojo.ErrorResponse;

@ControllerAdvice
public class UberAuthExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UberAuthExceptionHandler.class);

    @ExceptionHandler(UberAuthException.class)
    public ResponseEntity<ErrorResponse> handleUberAuthException(UberAuthException ex) {
        logger.error("Validation exception occurred: {}", ex.getErrorMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getErrorMessage())
                .build();

        logger.debug("handleUberAuthException response | errorResponse: {}", errorResponse);
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Generic exception occurred: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode())
                .errorMessage(ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage())
                .build();

        logger.debug("handleGenericException response | errorResponse: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
