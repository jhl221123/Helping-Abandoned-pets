package com.catdog.help.service;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.message.MsgRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.MsgRoomNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.ItemRepository;
import com.catdog.help.repository.MsgRoomRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.message.PageMsgRoomForm;
import com.catdog.help.web.form.message.ReadMessageForm;
import com.catdog.help.web.form.message.ReadMsgRoomForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MsgRoomService {

    private final MsgRoomRepository msgRoomRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Long save(Long boardId, String senderNick, String recipientNick) {
        MsgRoom msgRoom = getMessageRoom(boardId, senderNick, recipientNick);
        return msgRoomRepository.save(msgRoom).getId();
    }

    public Long checkRoom(Long boardId, String senderNick) {
        User findUser = userRepository.findByNickname(senderNick)
                .orElseThrow(UserNotFoundException::new);
        Optional<MsgRoom> findRoom = msgRoomRepository.findByRefers(findUser.getId(), boardId);

        return findRoom.isEmpty() ? -1L : findRoom.get().getId();
    }

    public ReadMsgRoomForm read(Long roomId) {
        MsgRoom findRoom = msgRoomRepository.findWithRefers(roomId)
                .orElseThrow(MsgRoomNotFoundException::new);
        List<ReadMessageForm> forms = getReadMessageForms(findRoom);
        return new ReadMsgRoomForm(findRoom, forms);
    }

    public Page<PageMsgRoomForm> getPage(String nickname, Pageable pageable) {
        Long userId = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new)
                .getId();

        return msgRoomRepository.findPageByUserId(userId, pageable)
                .map(PageMsgRoomForm::new);
    }


    /**============================= private method ==============================*/


    private MsgRoom getMessageRoom(Long boardId, String senderNick, String recipientNick) {
        Item findBoard = itemRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        User sender = userRepository.findByNickname(senderNick)
                .orElseThrow(UserNotFoundException::new);
        User recipient = userRepository.findByNickname(recipientNick)
                .orElseThrow(UserNotFoundException::new);

        return MsgRoom.builder()
                .item(findBoard)
                .sender(sender)
                .recipient(recipient)
                .build();
    }

    private List<ReadMessageForm> getReadMessageForms(MsgRoom msgRoom) {
        return msgRoom.getMessages().stream()
                .map(ReadMessageForm::new)
                .collect(Collectors.toList());
    }
}