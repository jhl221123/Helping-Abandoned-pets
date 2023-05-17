package com.catdog.help.web.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SaveBulletinResponse {

    private Long id;


    @Builder
    public SaveBulletinResponse(Long id) {
        this.id = id;
    }
}
