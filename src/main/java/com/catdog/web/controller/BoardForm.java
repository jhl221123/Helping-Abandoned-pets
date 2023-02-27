package com.catdog.web.controller;

import com.catdog.web.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.time.LocalDateTime;

@Getter @Setter
public class BoardForm {

    private Long boardNo;
    private User user;
    private String region;
    private String title;
    private String content;
    private String image; //Blob으로 교체!
    private LocalDateTime writeDate;
    private int score;
}
