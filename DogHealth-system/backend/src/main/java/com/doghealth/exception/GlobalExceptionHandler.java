package com.doghealth.exception;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DogHealthException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDogHealthException(
            DogHealthException ex) {
        
        return Try.of(() -> {
            log.error("DogHealthException: code={}, message={}", 
                    ex.getErrorCode().getCode(), ex.getMessage(), ex);
            
            HttpStatus status = mapErrorCodeToHttpStatus(ex.getErrorCode());
            
            Map<String, Object> errorResponse = Map.of(
                    "error", ex.getErrorCode().getCode(),
                    "message", ex.getMessage(),
                    "timestamp", LocalDateTime.now()
            );
            
            return ResponseEntity.status(status).body(errorResponse);
        })
        .recover(e -> {
            log.error("Error in exception handler", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "HANDLER_ERROR",
                            "message", "Error processing exception",
                            "timestamp", LocalDateTime.now()
                    ));
        })
        .map(Mono::just)
        .get();
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(
            Exception ex) {
        
        log.error("Unexpected error", ex);
        
        Map<String, Object> errorResponse = Map.of(
                "error", ErrorCode.UNKNOWN_ERROR.getCode(),
                "message", "An unexpected error occurred",
                "timestamp", LocalDateTime.now()
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse));
    }
    
    private HttpStatus mapErrorCodeToHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case DOG_NOT_FOUND, BREED_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DATABASE_ERROR, LLM_SERVICE_ERROR -> HttpStatus.SERVICE_UNAVAILABLE;
            case LINE_API_ERROR -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
