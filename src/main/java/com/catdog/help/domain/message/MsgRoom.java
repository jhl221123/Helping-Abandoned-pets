package com.catdog.help.domain.message;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
public class MsgRoom extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "message_room_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "msgRoom", cascade = REMOVE)
    private List<Message> messages = new ArrayList<>();


    @Builder
    public MsgRoom(Item item, User sender, User recipient) {
        this.item = item;
        this.sender = sender;
        this.recipient = recipient;
    }
}