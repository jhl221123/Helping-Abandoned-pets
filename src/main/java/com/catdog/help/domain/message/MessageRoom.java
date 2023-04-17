package com.catdog.help.domain.message;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageRoom {

    @Id @GeneratedValue
    @Column(name = "message_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private ItemBoard itemBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Embedded
    private Dates dates;

    @OneToMany(mappedBy = "messageRoom", cascade = CascadeType.REMOVE)
    private List<Message> messages = new ArrayList<>();


    @Builder
    public MessageRoom(ItemBoard itemBoard, User sender, User recipient) {
        this.itemBoard = itemBoard;
        this.sender = sender;
        this.recipient = recipient;
        this.dates = new Dates(LocalDateTime.now(), LocalDateTime.now(), null);
    }
}