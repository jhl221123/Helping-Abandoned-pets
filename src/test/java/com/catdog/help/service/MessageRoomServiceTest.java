package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.MessageRepository;
import com.catdog.help.repository.jpa.MessageRoomRepository;
import com.catdog.help.web.form.message.ReadMessageRoomForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class MessageRoomServiceTest {

    @Autowired MessageRoomService messageRoomService;
    @Autowired MessageRoomRepository messageRoomRepository;
    @Autowired MessageRepository messageRepository;
    @Autowired JpaItemBoardRepository itemBoardRepository;
    @Autowired UserRepository userRepository;

    @Test
    void readRoom() {
        //given
        User recipientA = createUser("recipientA@email", "password", "nickName1");
        User senderC = createUser("senderC@email", "password", "nickName2");
        userRepository.save(recipientA);
        userRepository.save(senderC);

        ItemBoard itemBoardByA = createItemBoard("인형", 100, recipientA);
        itemBoardRepository.save(itemBoardByA);

        MessageRoom roomAC = createMessageRoom(recipientA, senderC, itemBoardByA);
        messageRoomRepository.save(roomAC);

        Message messageCToA = createMessage(senderC, roomAC);
        Message messageAToC = createMessage(recipientA, roomAC);
        messageRepository.save(messageCToA);
        messageRepository.save(messageAToC);

        //when
        messageRoomRepository.flushAndClear();
        ReadMessageRoomForm roomFrom = messageRoomService.readRoom(roomAC.getId());

        //then
        System.out.println("roomFrom = " + roomFrom);
        Assertions.assertThat(roomFrom).isNotNull();
    }

    private Message createMessage(User senderC, MessageRoom roomByAAndC) {
        Message messageBySenderC = new Message();
        messageBySenderC.addMessage(roomByAAndC);
        messageBySenderC.setSender(senderC);
        messageBySenderC.setContent("인형 얼마인가요?");
        messageBySenderC.setDates(new Dates(LocalDateTime.now(), null, null));
        return messageBySenderC;
    }

    private static MessageRoom createMessageRoom(User recipientA, User senderC, ItemBoard itemBoardByA) {
        MessageRoom messageRoomByAAndC = new MessageRoom();
        messageRoomByAAndC.setRecipient(recipientA);
        messageRoomByAAndC.setSender(senderC);
        messageRoomByAAndC.setItemBoard(itemBoardByA);
        return messageRoomByAAndC;
    }

    private static ItemBoard createItemBoard(String itemName, int price, User user) {
        ItemBoard board = new ItemBoard();
        board.setItemName(itemName);
        board.setPrice(price);
        board.setTitle("title");
        board.setContent("content");
        board.setStatus(ItemStatus.STILL);
        board.setUser(user);
        board.setDates(new Dates(LocalDateTime.now(), null, null));
        return board;
    }

    private static User createUser(String emailId, String password, String nickName) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName("name");
        user.setNickName(nickName);
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        return user;
    }
}