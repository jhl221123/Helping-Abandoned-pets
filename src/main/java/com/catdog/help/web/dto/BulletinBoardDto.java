package com.catdog.help.web.dto;

import com.catdog.help.domain.Board.UploadFile;
import com.catdog.help.domain.User;
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
    private String region;
    private List<UploadFile> images = new ArrayList<>();
    private int score;
}
