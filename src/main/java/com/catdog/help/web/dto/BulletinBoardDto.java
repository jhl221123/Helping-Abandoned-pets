package com.catdog.help.web.dto;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class BulletinBoardDto {

    private Long id;
    private User user;
    private String title;
    private String content;
    private Dates dates;
    private int views;
    private String region;
    private List<UploadFile> images = new ArrayList<>();
    private int likeBoardSize;
}
