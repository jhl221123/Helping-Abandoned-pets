package com.catdog.help.web.form;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class BoardByRegion {

    private String region;
    private Long count;

    @QueryProjection
    public BoardByRegion(String region, Long count) {
        this.region = region;
        this.count = count;
    }
}
