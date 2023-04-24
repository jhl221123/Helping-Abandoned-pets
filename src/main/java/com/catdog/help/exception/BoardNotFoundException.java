package com.catdog.help.exception;

public class BoardNotFoundException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public BoardNotFoundException() {
        super(MESSAGE);
    }

    public BoardNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
