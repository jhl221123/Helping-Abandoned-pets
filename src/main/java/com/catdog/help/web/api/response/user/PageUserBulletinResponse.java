package com.catdog.help.web.api.response.user;

import com.catdog.help.web.form.bulletin.PageBulletinForm;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageUserBulletinResponse {

    private Long id;
    private String title;
    private String nickname;
    private LocalDateTime createdDate;
    private int views;
    private String region;


    public PageUserBulletinResponse(PageBulletinForm form) {
        this.id = form.getId();
        this.title = form.getTitle();
        this.nickname = form.getNickname();
        this.createdDate = form.getCreatedDate();
        this.views = form.getViews();
        this.region = form.getRegion();
    }
}
