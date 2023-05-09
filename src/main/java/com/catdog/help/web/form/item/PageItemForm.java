package com.catdog.help.web.form.item;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PageItemForm {

    private Long id;

    private String itemName;

    private int price;

    private String region;

    private ItemStatus status;

    private ReadImageForm leadImage;

    private int views;

    private int likeSize;

    private int rooms;


    public PageItemForm(Item board, ReadImageForm leadImage) {
        this.id = board.getId();
        this.itemName = board.getItemName();
        this.price = board.getPrice();
        this.region = board.getRegion();
        this.status = board.getStatus();
        this.leadImage = leadImage;
        this.views = board.getViews();
        this.likeSize = board.getLikes().size();
        this.rooms = board.getRooms().size();
    }
}
