package com.catdog.help.web.form.message;

import com.catdog.help.domain.message.MessageRoom;
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

    public PageMsgRoomForm(MessageRoom messageRoom) {
        this.id = messageRoom.getId();
        this.itemBoardId = messageRoom.getItem().getId();
        this.itemName = messageRoom.getItem().getItemName();
        this.senderNick = messageRoom.getSender().getNickname();
        this.recipientNick = messageRoom.getRecipient().getNickname();
        this.createdDate = messageRoom.getCreatedDate();
    }
}
