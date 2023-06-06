package com.catdog.help.web.api;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Base64Image {

    private String originalName;
    private String base64File;


    @Builder
    public Base64Image(String originalName, String base64File) {
        this.originalName = originalName;
        this.base64File = base64File;
    }
}
