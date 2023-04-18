package com.catdog.help.web.form.itemboard;

import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ReadItemBoardForm {
    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private String itemName;
    private int price;
    private ItemStatus status;
    private List<ReadUploadFileForm> images = new ArrayList<>();
    private int views;
    private int likeSize;

    public ReadItemBoardForm(ItemBoard findBoard, List<ReadUploadFileForm> images, int likeSize) {
        this.id = findBoard.getId();
        this.nickname = findBoard.getUser().getNickname();
        this.title = findBoard.getTitle();
        this.content = findBoard.getContent();
        this.createdDate = findBoard.getCreatedDate();
        this.itemName = findBoard.getItemName();
        this.price = findBoard.getPrice();
        this.status = findBoard.getStatus();
        this.images = images;
        this.views = findBoard.getViews();
        this.likeSize = likeSize;
    }
}
