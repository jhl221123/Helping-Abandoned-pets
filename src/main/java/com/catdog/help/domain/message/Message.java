package com.catdog.help.domain.message;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.message.SaveMessageForm;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

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


    @Builder
    public Message(MessageRoom messageRoom, User sender, SaveMessageForm form) {
        this.messageRoom = messageRoom;
        messageRoom.getMessages().add(this);
        this.sender = sender;
        this.content = form.getContent();
    }
}
