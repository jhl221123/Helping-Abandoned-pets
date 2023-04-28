package com.catdog.help.web.form.message;

import com.catdog.help.domain.message.MessageRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ReadMsgRoomForm {

    private Long id;
    private Long boardId;
    private String itemName;
    private String senderNick;
    private String recipientNick;
    private LocalDateTime createdDate;
    private List<ReadMessageForm> messages = new ArrayList<>();


    @Builder
    public ReadMsgRoomForm(MessageRoom messageRoom, List<ReadMessageForm> messageForms) {
        this.id = messageRoom.getId();
        this.boardId = messageRoom.getItem().getId();
        this.itemName = messageRoom.getItem().getItemName();
        this.senderNick = messageRoom.getSender().getNickname();
        this.recipientNick = messageRoom.getRecipient().getNickname();
        this.createdDate = messageRoom.getCreatedDate();
        this.messages = messageForms;
    }
}
