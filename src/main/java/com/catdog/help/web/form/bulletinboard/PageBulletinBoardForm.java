package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.DateList;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class PageBulletinBoardForm {

    private Long id;
    private String region;
    private String title;
    private String userNickName;
    private DateList dateList;
    private int views;
}
