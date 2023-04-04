package com.catdog.help.web.form.itemBoard;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ReadItemBoardForm {
    private Long id;
    private User user;
    private String title;
    private String content;
    private Dates dates;
    private String itemName;
    private int price;
    private ItemStatus status;
    private List<UploadFile> images = new ArrayList<>();
    private int views;
    private int likeSize;
}
