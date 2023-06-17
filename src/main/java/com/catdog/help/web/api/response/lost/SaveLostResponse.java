package com.catdog.help.web.api.response.lost;

import lombok.Getter;

@Getter
public class SaveLostResponse {

    private Long id;


    public SaveLostResponse(Long id) {
        this.id = id;
    }
}
