package com.catdog.help.exception;

public class NotFoundUserException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 사용자입니다.";

    public NotFoundUserException() {
        super(MESSAGE);
    }

    public NotFoundUserException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
