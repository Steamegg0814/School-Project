package com.doghealth.exception;

public enum ErrorCode {
    INVALID_INPUT("INVALID_INPUT", "Invalid input provided"),
    DOG_NOT_FOUND("DOG_NOT_FOUND", "Dog not found"),
    DATABASE_ERROR("DATABASE_ERROR", "Database operation failed"),
    LLM_SERVICE_ERROR("LLM_SERVICE_ERROR", "LLM service unavailable"),
    LINE_API_ERROR("LINE_API_ERROR", "Line API error"),
    CALCULATION_ERROR("CALCULATION_ERROR", "Calculation error"),
    BREED_NOT_FOUND("BREED_NOT_FOUND", "Breed information not found"),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "Unknown error occurred");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
