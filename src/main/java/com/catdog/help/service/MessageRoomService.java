package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.JpaMessageRoomRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.message.ReadMessageForm;
import com.catdog.help.web.form.message.ReadMessageRoomForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageRoomService {

    private final JpaMessageRoomRepository jpaMessageRoomRepository;
    private final JpaItemBoardRepository itemBoardRepository;
    private final JpaUserRepository userRepository;

    @Transactional
    public Long createRoom(Long itemBoardId, String senderNickName, String recipientNickName) {
        MessageRoom messageRoom = createMessageRoom(itemBoardId, senderNickName, recipientNickName);
        jpaMessageRoomRepository.save(messageRoom);
        return messageRoom.getId();
    }

    public Long checkRoom(Long boardId, String senderNickName) {
        User findUser = userRepository.findByNickname(senderNickName);
        List<MessageRoom> findRoom = jpaMessageRoomRepository.findAllByUserId(findUser.getId());
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

    public ReadMessageRoomForm readRoom(Long roomId) {
        MessageRoom findRoom = jpaMessageRoomRepository.findWithRefers(roomId);
        ReadMessageRoomForm result = getReadMessageRoomFrom(findRoom);
        return result;
    }

    public List<ReadMessageRoomForm> readPageOfRooms(String nickName, int page) {
        Long findUserId = userRepository.findByNickname(nickName).getId();
        int offset = page * 10 - 10;
        int limit = 10;

        List<MessageRoom> findRooms = jpaMessageRoomRepository.findPageByUserId(findUserId, offset, limit);
        List<ReadMessageRoomForm> readForms = findRooms.stream()
                .map(messageRoom -> {
                    ReadMessageRoomForm form = getReadMessageRoomFrom(messageRoom);
                    return form;
                }).collect(Collectors.toList());
        return readForms;
    }

    public int countPages(String nickName) {
        Long findUserId = userRepository.findByNickname(nickName).getId();
        int totalRooms = (int) jpaMessageRoomRepository.countAllByUserId(findUserId);
        if (totalRooms <= 10) {
            return 1;
        } else if (totalRooms % 10 == 0) {
            return totalRooms / 10;
        } else {
            return totalRooms / 10 + 1;
        }
    }


    /**============================= private method ==============================*/


    private MessageRoom createMessageRoom(Long itemBoardId, String senderNickName, String recipientNickName) {
        ItemBoard findBoard = itemBoardRepository.findById(itemBoardId);
        User sender = userRepository.findByNickname(senderNickName);
        User recipient = userRepository.findByNickname(recipientNickName);

        MessageRoom messageRoom = new MessageRoom();
        messageRoom.setItemBoard(findBoard);
        messageRoom.setSender(sender);
        messageRoom.setRecipient(recipient);
        messageRoom.setDates(new Dates(LocalDateTime.now(), null, null));
        return messageRoom;
    }

    private ReadMessageRoomForm getReadMessageRoomFrom(MessageRoom messageRoom) {
        ReadMessageRoomForm form = new ReadMessageRoomForm();
        form.setId(messageRoom.getId());
        form.setItemBoardId(messageRoom.getItemBoard().getId());
        form.setItemName(messageRoom.getItemBoard().getItemName());
        form.setSenderNick(messageRoom.getSender().getNickname());
        form.setRecipientNick(messageRoom.getRecipient().getNickname());
        form.setCreateDate(messageRoom.getDates().getCreateDate());
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
        form.setSenderNick(message.getSender().getNickname()); //메시지 강제호출
        form.setContent(message.getContent());
        form.setCreateDate(message.getDates().getCreateDate());
        return form;
    }
}
