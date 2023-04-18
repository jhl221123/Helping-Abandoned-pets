package com.catdog.help.service;

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
    public Long createRoom(Long boardId, String senderNick, String recipientNick) {
        MessageRoom messageRoom = getMessageRoom(boardId, senderNick, recipientNick);
        return jpaMessageRoomRepository.save(messageRoom);
    }

    public Long checkRoom(Long boardId, String senderNick) {
        User findUser = userRepository.findByNickname(senderNick);
        List<MessageRoom> findRoom = jpaMessageRoomRepository.findAllByUserId(findUser.getId());

        MessageRoom target = findRoom.stream()
                .filter(m -> m.getItemBoard().getId() == boardId)
                .findAny()
                .orElse(null);

        return target == null ? -1L : target.getId();
    }

    public ReadMessageRoomForm readRoom(Long roomId) {
        MessageRoom findRoom = jpaMessageRoomRepository.findWithRefers(roomId);
        List<ReadMessageForm> forms = getReadMessageForms(findRoom);
        return new ReadMessageRoomForm(findRoom, forms);
    }

    public List<PageMessageRoomForm> readRoomPage(String nickname, int page) {
        Long userId = userRepository.findByNickname(nickname).getId();
        int offset = page * 10 - 10;
        int limit = 10;

        return getPageMessageRoomForms(jpaMessageRoomRepository.findPageByUserId(userId, offset, limit));
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


    private MessageRoom getMessageRoom(Long boardId, String senderNick, String recipientNick) {
        ItemBoard findBoard = itemBoardRepository.findById(boardId);
        User sender = userRepository.findByNickname(senderNick);
        User recipient = userRepository.findByNickname(recipientNick);

        return MessageRoom.builder()
                .itemBoard(findBoard)
                .sender(sender)
                .recipient(recipient)
                .build();
    }

    private static List<PageMessageRoomForm> getPageMessageRoomForms(List<MessageRoom> findRooms) {
        return findRooms.stream()
                .map(PageMessageRoomForm::new)
                .collect(Collectors.toList());
    }

    private List<ReadMessageForm> getReadMessageForms(MessageRoom messageRoom) {
        return messageRoom.getMessages().stream()
                .map(ReadMessageForm::new)
                .collect(Collectors.toList());
    }
}
