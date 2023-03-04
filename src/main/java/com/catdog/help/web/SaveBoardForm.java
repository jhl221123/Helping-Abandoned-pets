package com.catdog.help.web;

import com.catdog.help.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class SaveBoardForm {

    private User user;
    private String region;
    private String title;
    private String content;
    private String image; //Blob으로 교체!
}
