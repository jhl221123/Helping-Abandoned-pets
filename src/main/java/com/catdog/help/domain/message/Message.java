package com.catdog.help.domain.message;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.message.SaveMessageForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Embedded
    private Dates dates;


    @Builder
    public Message(MessageRoom messageRoom, User sender, SaveMessageForm form) {
        this.messageRoom = messageRoom;
        messageRoom.getMessages().add(this);
        this.sender = sender;
        this.content = form.getContent();
        this.dates = new Dates(LocalDateTime.now(), LocalDateTime.now(), null);
    }
}
