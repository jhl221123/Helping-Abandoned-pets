package com.catdog.help.web.form.search;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class BulletinSearch {

    private String title;

    private String region;

    @Builder
    public BulletinSearch(String region, String title) {
        this.region = region;
        this.title = title;
    }
}
