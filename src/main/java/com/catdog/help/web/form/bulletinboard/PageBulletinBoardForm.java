package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.Dates;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PageBulletinBoardForm {

    private Long id;
    private String region;
    private String title;
    private String userNickName;
    private Dates dates;
    private int views;
}
