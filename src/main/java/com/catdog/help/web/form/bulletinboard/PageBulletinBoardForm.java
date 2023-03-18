package com.catdog.help.web.form.bulletinboard;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class PageBulletinBoardForm {

    private Long id;
    private String region;
    private String title;
    private String userNickName;
    private LocalDateTime writeDate;
    private int views;
}
