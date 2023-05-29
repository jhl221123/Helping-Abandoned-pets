package com.catdog.help.web.api.response.user;

import lombok.Getter;

@Getter
public class LoginResponse {

    private String redirectURL;


    public LoginResponse(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}
