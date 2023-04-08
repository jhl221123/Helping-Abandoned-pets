package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.MessageRepository;
import com.catdog.help.repository.jpa.MessageRoomRepository;
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

    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createMessage(Long roomId, String senderNickName, SaveMessageForm saveForm) {
        MessageRoom findRoom = messageRoomRepository.findById(roomId);
        User sender = userRepository.findByNickName(senderNickName);
        Message message = new Message();
        message.setMessageRoom(findRoom);
        message.setSender(sender);
        message.setContent(saveForm.getContent());
        message.setDates(new Dates(LocalDateTime.now(), null, null));
        messageRepository.save(message);
    }
}
