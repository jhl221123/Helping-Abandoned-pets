package com.catdog.help.exception;

public class LoginFailureException extends RuntimeException{

    private static final String MESSAGE = "일치하지 않는 아이디 또는 비밀번호입니다.";

    public LoginFailureException() {
        super(MESSAGE);
    }

    public LoginFailureException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
