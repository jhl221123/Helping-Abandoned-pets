package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.JpaMessageRoomRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.message.PageMessageRoomForm;
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
    public Long createRoom(Long itemBoardId, String senderNick, String recipientNick) {
        MessageRoom messageRoom = createMessageRoom(itemBoardId, senderNick, recipientNick);
        jpaMessageRoomRepository.save(messageRoom);
        return messageRoom.getId();
    }

    public Long checkRoom(Long boardId, String senderNick) {
        User findUser = userRepository.findByNickname(senderNick);
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
        List<ReadMessageForm> forms = getReadMessageForms(findRoom);
        ReadMessageRoomForm result = new ReadMessageRoomForm(findRoom, forms);
        return result;
    }

    public List<PageMessageRoomForm> readRoomPage(String nickname, int page) {
        Long findUserId = userRepository.findByNickname(nickname).getId();
        int offset = page * 10 - 10;
        int limit = 10;

        List<MessageRoom> findRooms = jpaMessageRoomRepository.findPageByUserId(findUserId, offset, limit);
        return getPageMessageRoomForms(findRooms);
    }

    public int countPages(String nickname) {
        Long findUserId = userRepository.findByNickname(nickname).getId();
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


    private MessageRoom createMessageRoom(Long itemBoardId, String senderNick, String recipientNick) {
        ItemBoard findBoard = itemBoardRepository.findById(itemBoardId);
        User sender = userRepository.findByNickname(senderNick);
        User recipient = userRepository.findByNickname(recipientNick);

        return MessageRoom.builder()
                .itemBoard(findBoard)
                .sender(sender)
                .recipient(recipient)
                .build();
    }

    private static List<PageMessageRoomForm> getPageMessageRoomForms(List<MessageRoom> findRooms) {
        List<PageMessageRoomForm> pageForms = findRooms.stream()
                .map(PageMessageRoomForm::new)
                .collect(Collectors.toList());
        return pageForms;
    }

    private List<ReadMessageForm> getReadMessageForms(MessageRoom messageRoom) {
        return messageRoom.getMessages().stream()
                .map(ReadMessageForm::new)
                .collect(Collectors.toList());
    }
}
