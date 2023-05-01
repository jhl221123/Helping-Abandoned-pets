package com.catdog.help.web.form.message;

import com.catdog.help.domain.message.MsgRoom;
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
    public ReadMsgRoomForm(MsgRoom msgRoom, List<ReadMessageForm> messageForms) {
        this.id = msgRoom.getId();
        this.boardId = msgRoom.getItem().getId();
        this.itemName = msgRoom.getItem().getItemName();
        this.senderNick = msgRoom.getSender().getNickname();
        this.recipientNick = msgRoom.getRecipient().getNickname();
        this.createdDate = msgRoom.getCreatedDate();
        this.messages = messageForms;
    }
}
