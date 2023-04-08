package com.catdog.help.web.form.itemboard;

import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PageItemBoardForm {

    private Long id;

    private String itemName;

    private int price;

    private ReadUploadFileForm leadImage;

    private ItemStatus status;
}
