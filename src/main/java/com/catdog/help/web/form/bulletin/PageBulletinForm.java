package com.catdog.help.web.form.bulletin;

import com.catdog.help.domain.board.Bulletin;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageBulletinForm {

    private Long id;
    private String title;
    private String nickname;
    private LocalDateTime createdDate;
    private int views;
    private String region;


    public PageBulletinForm(Bulletin board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.nickname = board.getUser().getNickname();
        this.createdDate = board.getCreatedDate();
        this.views = board.getViews();
        this.region = board.getRegion();
    }
}
