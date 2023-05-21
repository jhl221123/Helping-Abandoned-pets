package com.catdog.help.exception;

public class EmailDuplicateException extends RuntimeException {

    private final String field = "email";

    private static final String MESSAGE = "이미 존재하는 이메일입니다.";


    public EmailDuplicateException() {
        super(MESSAGE);
    }

    public EmailDuplicateException(Throwable cause) {
        super(MESSAGE, cause);
    }


    public String getField() {
        return field;
    }
}
