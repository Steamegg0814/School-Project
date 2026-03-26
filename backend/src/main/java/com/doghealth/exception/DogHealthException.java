package com.doghealth.exception;

public class DogHealthException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public DogHealthException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public DogHealthException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
