package com.catdog.help.web.api.response.item;

import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.item.PageItemForm;
import lombok.Getter;

@Getter
public class PageItemResponse {

    private Long id;

    private String itemName;

    private int price;

    private String region;

    private ItemStatus status;

    private ReadImageForm leadImage;

    private int views;

    private int likeSize;

    private int roomSize;


    public PageItemResponse(PageItemForm form, ReadImageForm leadImage) {
        this.id = form.getId();
        this.itemName = form.getItemName();
        this.price = form.getPrice();
        this.region = form.getRegion();
        this.status = form.getStatus();
        this.leadImage = leadImage;
        this.views = form.getViews();
        this.likeSize = form.getLikeSize();
        this.roomSize = form.getRoomSize();
    }
}
