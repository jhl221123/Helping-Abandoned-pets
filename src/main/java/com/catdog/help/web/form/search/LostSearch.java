package com.catdog.help.web.form.search;

import lombok.Getter;

@Getter
public class LostSearch {

    private String region;


    public LostSearch(String region) {
        this.region = region;
    }
}
