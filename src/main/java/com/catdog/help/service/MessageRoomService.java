package com.catdog.help.service;

import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.MessageRoomRepository;
import com.catdog.help.web.form.message.ReadMessageForm;
import com.catdog.help.web.form.message.ReadMessageRoomFrom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    private final JpaItemBoardRepository itemBoardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createRoom(Long itemBoardId, String senderNickName, String recipientNickName) {
        MessageRoom messageRoom = createMessageRoom(itemBoardId, senderNickName, recipientNickName);
        messageRoomRepository.save(messageRoom);
        return messageRoom.getId();
    }

    public Long checkRoom(Long boardId, String senderNickName) {
        User findUser = userRepository.findByNickName(senderNickName);
        List<MessageRoom> findRoom = messageRoomRepository.findAllByUserId(findUser.getId());
        MessageRoom target = findRoom.stream()
                .filter(m -> m.getItemBoard().getId() == boardId)
                .findAny()
                .orElse(null);
        if (target == null) {
            return -1L;
        } else {
            return target.getId();
        }
    }

    public ReadMessageRoomFrom readRoom(Long roomId) {
        MessageRoom findRoom = messageRoomRepository.findWithRefers(roomId);
        ReadMessageRoomFrom result = getReadMessageRoomFrom(findRoom);
        return result;
    }

    public List<ReadMessageRoomFrom> readRoomsByNickName(String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        List<MessageRoom> findRooms = messageRoomRepository.findAllByUserId(findUser.getId());
        List<ReadMessageRoomFrom> readForms = findRooms.stream()
                .map(messageRoom -> {
                    ReadMessageRoomFrom form = getReadMessageRoomFrom(messageRoom);
                    return form;
                }).collect(Collectors.toList());
        return readForms;
    }


    /**============================= private method ==============================*/


    private MessageRoom createMessageRoom(Long itemBoardId, String senderNickName, String recipientNickName) {
        ItemBoard findBoard = itemBoardRepository.findById(itemBoardId);
        User sender = userRepository.findByNickName(senderNickName);
        User recipient = userRepository.findByNickName(recipientNickName);

        MessageRoom messageRoom = new MessageRoom();
        messageRoom.setItemBoard(findBoard);
        messageRoom.setSender(sender);
        messageRoom.setRecipient(recipient);
        return messageRoom;
    }

    private ReadMessageRoomFrom getReadMessageRoomFrom(MessageRoom messageRoom) {
        ReadMessageRoomFrom form = new ReadMessageRoomFrom();
        form.setId(messageRoom.getId());
        form.setItemBoardId(messageRoom.getItemBoard().getId());
        form.setItemName(messageRoom.getItemBoard().getItemName());
        form.setSenderNick(messageRoom.getSender().getNickName());
        form.setRecipientNick(messageRoom.getRecipient().getNickName());
        List<ReadMessageForm> messageForms = getReadMessageForms(messageRoom);
        form.setMessages(messageForms);
        return form;
    }

    private List<ReadMessageForm> getReadMessageForms(MessageRoom messageRoom) {
        List<ReadMessageForm> messageForms = messageRoom.getMessages().stream()
                .map(message -> {
                    ReadMessageForm form = getReadMessageForm(message);
                    return form;
                }).collect(Collectors.toList());
        return messageForms;
    }

    private static ReadMessageForm getReadMessageForm(Message message) {
        ReadMessageForm form = new ReadMessageForm();
        form.setSenderNick(message.getSender().getNickName());
        form.setContent(message.getContent());
        form.setCreateDate(message.getDates().getCreateDate());
        return form;
    }
}
