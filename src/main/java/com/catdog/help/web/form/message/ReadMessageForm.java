package com.catdog.help.web.form.message;

import com.catdog.help.domain.message.Message;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class ReadMessageForm {

    private String senderNick;
    private String content;
    private LocalDateTime createDate;

    public ReadMessageForm(Message message) {
        this.senderNick = message.getSender().getNickname(); //메시지 강제호출
        this.content = message.getContent();
        this.createDate = message.getDates().getCreateDate();
    }
}
