package com.authservice.exception;

import org.springframework.http.HttpStatus;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.authservice.constants.ErrorCodeEnum;
import com.authservice.pojo.ErrorResponse;
import com.authservice.utils.LogMessage;

@ControllerAdvice
public class UberAuthExceptionHandler {
	
    private static final Logger LOGGER = LogManager.getLogger(UberAuthExceptionHandler.class);

    @ExceptionHandler(UberAuthException.class)
    public ResponseEntity<ErrorResponse> handleUberAuthException(UberAuthException ex) {
        LogMessage.logException(LOGGER, ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .errorMessage(ex.getErrorMessage())
                .build();
        
        LogMessage.debug(LOGGER, String.format("handleUberAuthException response: "
        		+ "errorResponse=%f", errorResponse));

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        LogMessage.logException(LOGGER, ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode())
                .errorMessage(ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage())
                .build();

        LogMessage.debug(LOGGER, String.format("handleGenericException response:"
        		+ " errorResponse=%f", errorResponse));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
