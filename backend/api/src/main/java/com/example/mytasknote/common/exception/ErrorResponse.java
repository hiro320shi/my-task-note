package com.example.mytasknote.common.exception;

import java.util.Map;

public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> errors;

    public ErrorResponse() {}

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Map<String, String> getErrors() { return errors; }

    public void setCode(String code) { this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
