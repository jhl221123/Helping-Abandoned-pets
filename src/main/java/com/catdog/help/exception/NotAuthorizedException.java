package com.catdog.help.exception;

public class NotAuthorizedException extends RuntimeException {
    private String field;
    private static final String MESSAGE = "해당 요청에 대한 권한이 없습니다.";

    public NotAuthorizedException(String field) {
        super(MESSAGE);
        this.field = field;
    }

    public NotAuthorizedException(Throwable cause) {
        super(MESSAGE, cause);
    }
}

