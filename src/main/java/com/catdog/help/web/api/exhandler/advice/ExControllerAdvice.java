package com.catdog.help.web.api.exhandler.advice;

import com.catdog.help.exception.*;
import com.catdog.help.web.api.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice(basePackages = "com.catdog.help.web.api.controller")
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ErrorResult> invalidateExHandler(MethodArgumentNotValidException e) {
        List<ErrorResult> result = new ArrayList<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            ErrorResult error = ErrorResult.builder()
                    .code("BAD")
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
            result.add(error);
        }
        return result;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResult notReadableExHandler(HttpMessageNotReadableException e) {
        return ErrorResult.builder()
                .code("BAD")
                .message("Required request body is missing")
                .build();
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ErrorResult emailDuplicateExHandler(EmailDuplicateException e) {
        return ErrorResult.builder()
                .code("DUPLICATE")
                .field(e.getField())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NicknameDuplicateException.class)
    public ErrorResult nicknameDuplicateExHandler(NicknameDuplicateException e) {
        return ErrorResult.builder()
                .code("DUPLICATE")
                .field(e.getField())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordIncorrectException.class)
    public ErrorResult passwordIncorrectExHandler(PasswordIncorrectException e) {
        return ErrorResult.builder()
                .code("BAD")
                .field(e.getField())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordNotSameException.class)
    public ErrorResult passwordNotSameExHandler(PasswordNotSameException e) {
        return ErrorResult.builder()
                .code("BAD")
                .field(e.getField())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginFailureException.class)
    public ErrorResult loginFailureExHandler(LoginFailureException e) {
        return ErrorResult.builder()
                .code("FAIL_LOGIN")
                .message(e.getMessage())
                .build();
    }
}
