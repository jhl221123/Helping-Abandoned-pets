package com.catdog.help.web.form.message;

import com.catdog.help.domain.message.MessageRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageMessageRoomForm {
    private Long id;
    private Long itemBoardId;
    private String itemName;
    private String senderNick;
    private String recipientNick;
    private LocalDateTime createDate;

    public PageMessageRoomForm(MessageRoom messageRoom) {
        this.id = messageRoom.getId();
        this.itemBoardId = messageRoom.getItemBoard().getId();
        this.itemName = messageRoom.getItemBoard().getItemName();
        this.senderNick = messageRoom.getSender().getNickname();
        this.recipientNick = messageRoom.getRecipient().getNickname();
        this.createDate = messageRoom.getDates().getCreateDate();
    }
}
