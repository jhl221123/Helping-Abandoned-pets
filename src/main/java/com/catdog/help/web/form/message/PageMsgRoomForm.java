package com.catdog.help.web.form.message;

import com.catdog.help.domain.message.MsgRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageMsgRoomForm {
    private Long id;
    private Long itemBoardId;
    private String itemName;
    private String senderNick;
    private String recipientNick;
    private LocalDateTime createdDate;

    public PageMsgRoomForm(MsgRoom msgRoom) {
        this.id = msgRoom.getId();
        this.itemBoardId = msgRoom.getItem().getId();
        this.itemName = msgRoom.getItem().getItemName();
        this.senderNick = msgRoom.getSender().getNickname();
        this.recipientNick = msgRoom.getRecipient().getNickname();
        this.createdDate = msgRoom.getCreatedDate();
    }
}
