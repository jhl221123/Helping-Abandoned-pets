package com.catdog.help.web.form.itemBoard;

import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.board.UploadFile;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PageItemBoardForm {

    private Long id;

    private String itemName;

    private int price;

    private UploadFile leadImage;

    private ItemStatus status;
}
