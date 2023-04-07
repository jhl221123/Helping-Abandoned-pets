package com.catdog.help.domain.message;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Message {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_room_id")
    private MessageRoom messageRoom;

    private String content;

    private Dates dates;

    public void addMessage(MessageRoom messageRoom) {
        this.messageRoom = messageRoom;
        messageRoom.getMessages().add(this);
    }
}
