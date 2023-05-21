package com.catdog.help.web.api.exhandler;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResult {

    private String code;

    private String field;

    private String message;


    @Builder
    public ErrorResult(String code, String field, String message) {
        this.code = code;
        this.field = field;
        this.message = message;
    }
}
