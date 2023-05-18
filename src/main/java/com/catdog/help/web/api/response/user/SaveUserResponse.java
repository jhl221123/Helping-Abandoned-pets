package com.catdog.help.web.api.response.user;

import lombok.Getter;

@Getter
public class SaveUserResponse {

    private Long id;

    public SaveUserResponse(Long id) {
        this.id = id;
    }
}
