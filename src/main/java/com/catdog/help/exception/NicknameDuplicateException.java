package com.catdog.help.exception;

public class NicknameDuplicateException extends RuntimeException {

    private final String field = "nickname";

    private static final String MESSAGE = "이미 존재하는 닉네임입니다.";


    public NicknameDuplicateException() {
        super(MESSAGE);
    }

    public NicknameDuplicateException(Throwable cause) {
        super(MESSAGE, cause);
    }


    public String getField() {
        return field;
    }
}
