package com.catdog.help.service;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.MessageRepository;
import com.catdog.help.repository.MsgRoomRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.message.SaveMessageForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    MessageService messageService;

    @Mock
    MessageRepository messageRepository;

    @Mock
    MsgRoomRepository msgRoomRepository;

    @Mock
    UserRepository userRepository;


    @Test
    @DisplayName("해당 사용자가 전송한 메시지 저장")
    void saveMessage() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");

        Item board = getItem(recipient, "나눔상품");

        MessageRoom room = getMessageRoom(sender, recipient, board);

        Message message = getMessage(sender, room);

        SaveMessageForm form = new SaveMessageForm("메시지내용");

        doReturn(Optional.ofNullable(room)).when(msgRoomRepository)
                .findById(room.getId());

        doReturn(Optional.ofNullable(sender)).when(userRepository)
                .findByNickname(sender.getNickname());

        when(messageRepository.save(any(Message.class))).then(AdditionalAnswers.returnsFirstArg());

        //expected
        messageService.save(room.getId(), sender.getNickname(), form);

        verify(msgRoomRepository, times(1)).findById(room.getId());
        verify(userRepository, times(1)).findByNickname(sender.getNickname());
        verify(messageRepository, times(1)).save(any(Message.class));
    }


    private Message getMessage(User sender, MessageRoom room) {
        return Message.builder()
                .sender(sender)
                .messageRoom(room)
                .content("메시지내용")
                .build();
    }

    private MessageRoom getMessageRoom(User sender, User recipient, Item board) {
        return MessageRoom.builder()
                .item(board)
                .sender(sender)
                .recipient(recipient)
                .build();
    }

    private Item getItem(User user, String title) {
        return Item.builder()
                .user(user)
                .title(title)
                .content("내용")
                .itemName("테스트상품")
                .price(1000)
                .build();
    }

    private User getUser(String emailId, String nickname) {
        return User.builder()
                .emailId(emailId)
                .password("12345678")
                .nickname(nickname)
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}