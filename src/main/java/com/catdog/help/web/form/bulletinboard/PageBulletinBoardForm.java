package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PageBulletinBoardForm {

    private Long id;
    private String title;
    private String nickname;
    private Dates dates;
    private int views;
    private String region;

    public PageBulletinBoardForm(BulletinBoard board, String nickName) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.nickname = nickName;
        this.dates = board.getDates();
        this.views = board.getViews();
        this.region = board.getRegion();
    }
}
