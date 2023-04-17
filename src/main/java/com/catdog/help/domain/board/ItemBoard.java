package com.catdog.help.domain.board;

import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.web.form.itemboard.SaveItemBoardForm;
import com.catdog.help.web.form.itemboard.UpdateItemBoardForm;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Share")
public class ItemBoard extends Board {

    private String itemName;

    private int price;

    @Enumerated(value = EnumType.STRING)
    private ItemStatus status;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "itemBoard", cascade = CascadeType.REMOVE)
    private List<MessageRoom> rooms = new ArrayList<>();


    @Builder
    public ItemBoard(User user, SaveItemBoardForm form) {
        super(user, form.getTitle(), form.getContent());
        this.itemName = form.getItemName();
        this.price = form.getPrice();
        this.status = ItemStatus.STILL;
    }


    public void updateBoard(UpdateItemBoardForm form) {
        super.updateBoard(form.getTitle(), form.getContent());
        this.itemName = form.getItemName();
        this.price = form.getPrice();
    }

    public void changeStatus(ItemStatus status) {
        this.status = status;
    }
}
