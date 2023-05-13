package com.catdog.help.domain.board;

import com.catdog.help.domain.message.MsgRoom;
import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("Item")
public class Item extends Board {

    private String itemName;

    private int price;

    private String region;

    @Enumerated(value = EnumType.STRING)
    private ItemStatus status;

    @OneToMany(mappedBy = "board", cascade = REMOVE)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = REMOVE)
    private List<MsgRoom> rooms = new ArrayList<>();


    @Builder
    public Item(User user, String title, String content, String itemName, int price, String region) {
        super(user, title, content);
        this.itemName = itemName;
        this.price = price;
        this.region = region;
        this.status = ItemStatus.STILL;
    }


    public void updateBoard(String title, String content, String itemName, int price, String region) {
        super.updateBoard(title, content);
        this.itemName = itemName;
        this.price = price;
        this.region = region;
    }

    public void changeStatus(ItemStatus status) {
        this.status = status;
    }

    public void addLike(Like like) {
        this.likes.add(like);
    }
}
