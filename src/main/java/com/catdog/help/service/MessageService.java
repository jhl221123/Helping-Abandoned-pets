package com.catdog.help.service;

import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.MsgRoomNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.MessageRepository;
import com.catdog.help.repository.MsgRoomRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.message.SaveMessageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MsgRoomRepository msgRoomRepository;
    private final UserRepository userRepository;


    @Transactional
    public void save(Long roomId, String senderNick, SaveMessageForm form) {
        MessageRoom findRoom = msgRoomRepository.findById(roomId)
                .orElseThrow(MsgRoomNotFoundException::new);
        User sender = userRepository.findByNickname(senderNick)
                .orElseThrow(UserNotFoundException::new);
        messageRepository.save(getMessage(form, findRoom, sender));
    }


    private Message getMessage(SaveMessageForm form, MessageRoom findRoom, User sender) {
        return Message.builder()
                .messageRoom(findRoom)
                .sender(sender)
                .content(form.getContent())
                .build();
    }
}
