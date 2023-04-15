package com.catdog.help.web.form.message;

import com.catdog.help.domain.message.MessageRoom;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ReadMessageRoomForm {

    private Long id;
    private Long itemBoardId;
    private String itemName;
    private String senderNick;
    private String recipientNick;
    private LocalDateTime createDate;
    private List<ReadMessageForm> messages = new ArrayList<>();

    public ReadMessageRoomForm(MessageRoom messageRoom, List<ReadMessageForm> messageForms) {
        this.id = messageRoom.getId();
        this.itemBoardId = messageRoom.getItemBoard().getId();
        this.itemName = messageRoom.getItemBoard().getItemName();
        this.senderNick = messageRoom.getSender().getNickname();
        this.recipientNick = messageRoom.getRecipient().getNickname();
        this.createDate = messageRoom.getDates().getCreateDate();
        this.messages = messageForms;
    }
}
