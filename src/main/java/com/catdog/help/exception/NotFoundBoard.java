package com.catdog.help.exception;

public class NotFoundBoard extends RuntimeException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public NotFoundBoard() {
        super(MESSAGE);
    }

    public NotFoundBoard(Throwable cause) {
        super(MESSAGE, cause);
    }
}
