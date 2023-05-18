package com.catdog.help.web.api.response.bulletin;

import lombok.Getter;

@Getter
public class SaveBulletinResponse {

    private Long id;


    public SaveBulletinResponse(Long id) {
        this.id = id;
    }
}
