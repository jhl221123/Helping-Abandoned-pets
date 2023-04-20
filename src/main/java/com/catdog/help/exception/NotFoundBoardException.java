package com.catdog.help.exception;

public class NotFoundBoardException extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public NotFoundBoardException() {
        super(MESSAGE);
    }

    public NotFoundBoardException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
