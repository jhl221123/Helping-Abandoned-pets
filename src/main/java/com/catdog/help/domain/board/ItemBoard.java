package com.catdog.help.domain.board;

import com.catdog.help.domain.message.MessageRoom;
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
@DiscriminatorValue("Share")
public class ItemBoard extends Board {

    private String itemName;

    private int price;

    @Enumerated(value = EnumType.STRING)
    private ItemStatus status;

    @OneToMany(mappedBy = "board", cascade = REMOVE)
    private List<LikeBoard> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "itemBoard", cascade = REMOVE)
    private List<MessageRoom> rooms = new ArrayList<>();


    @Builder
    public ItemBoard(User user, String title, String content, String itemName, int price) {
        super(user, title, content);
        this.itemName = itemName;
        this.price = price;
        this.status = ItemStatus.STILL;
    }


    public void updateBoard(String title, String content, String itemName, int price) {
        super.updateBoard(title, content);
        this.itemName = itemName;
        this.price = price;
    }

    public void changeStatus(ItemStatus status) {
        this.status = status;
    }
}
