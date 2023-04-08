package com.catdog.help.web.form.message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReadMessageForm {

    private String senderNick;
    private String content;
    private LocalDateTime createDate;
}
