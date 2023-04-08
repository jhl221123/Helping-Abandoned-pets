package com.catdog.help.web.form.message;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ReadMessageRoomForm {

    private Long id;
    private Long itemBoardId;
    private String itemName;
    private String senderNick;
    private String recipientNick;
    private LocalDateTime createDate;
    private List<ReadMessageForm> messages = new ArrayList<>();
}
