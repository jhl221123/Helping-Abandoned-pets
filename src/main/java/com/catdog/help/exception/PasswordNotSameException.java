package com.catdog.help.exception;

import com.catdog.help.MyConst;

public class PasswordNotSameException extends RuntimeException {

    private final String field = MyConst.PASSWORD;

    private static final String MESSAGE = "변경하려는 비밀번호가 일치하지 않습니다.";


    public PasswordNotSameException() {
        super(MESSAGE);
    }

    public PasswordNotSameException(Throwable cause) {
        super(MESSAGE, cause);
    }


    public String getField() {
        return field;
    }
}
