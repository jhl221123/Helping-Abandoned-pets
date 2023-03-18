package com.catdog.help.web.dto;

import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class BulletinBoardDto {

    private Long id;
    private User user;
    private String title;
    private String content;
    private LocalDateTime writeDate;
    private int views;
    private String region;
    private List<UploadFile> images = new ArrayList<>();
    private int likeBoardSize;
}
