package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaMessageRepository;
import com.catdog.help.repository.jpa.JpaMessageRoomRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.message.SaveMessageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final JpaMessageRepository jpaMessageRepository;
    private final JpaMessageRoomRepository jpaMessageRoomRepository;
    private final JpaUserRepository userRepository;


    @Transactional
    public void createMessage(Long roomId, String senderNickName, SaveMessageForm form) {
        MessageRoom findRoom = jpaMessageRoomRepository.findById(roomId);
        User sender = userRepository.findByNickname(senderNickName);
        jpaMessageRepository.save(getMessage(form, findRoom, sender));
    }


    private static Message getMessage(SaveMessageForm form, MessageRoom findRoom, User sender) {
        Message message = Message.builder()
                .messageRoom(findRoom)
                .sender(sender)
                .form(form)
                .build();
        return message;
    }
}
