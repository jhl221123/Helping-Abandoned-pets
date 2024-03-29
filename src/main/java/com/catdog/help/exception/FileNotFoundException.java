package com.catdog.help.exception;

public class FileNotFoundException extends RuntimeException {
    private static final String MESSAGE = "존재하지 않는 파일입니다.";

    public FileNotFoundException() {
        super(MESSAGE);
    }

    public FileNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
