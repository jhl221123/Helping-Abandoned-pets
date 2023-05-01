package com.catdog.help.domain.message;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
public class Message extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "message_room_id")
    private MsgRoom msgRoom;

    private String content;


    @Builder
    public Message(MsgRoom msgRoom, User sender, String content) {
        this.msgRoom = msgRoom;
        msgRoom.getMessages().add(this);
        this.sender = sender;
        this.content = content;
    }
}
