package com.catdog.help.exception;

import com.catdog.help.MyConst;

public class PasswordIncorrectException extends RuntimeException {

    private final String field = MyConst.PASSWORD;

    private static final String MESSAGE = "기존 비밀번호와 일치하지 않습니다.";


    public PasswordIncorrectException() {
        super(MESSAGE);
    }

    public PasswordIncorrectException(Throwable cause) {
        super(MESSAGE, cause);
    }


    public String getField() {
        return field;
    }
}
