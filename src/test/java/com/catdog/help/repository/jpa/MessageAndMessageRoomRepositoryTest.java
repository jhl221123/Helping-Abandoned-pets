package com.catdog.help.repository.jpa;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MessageAndMessageRoomRepositoryTest {

    @Autowired MessageRoomRepository messageRoomRepository;
    @Autowired MessageRepository messageRepository;
    @Autowired JpaItemBoardRepository itemBoardRepository;
    @Autowired UserRepository userRepository;

    @Test
    void 저장_조회() {
        //given
        User recipientA = createUser("recipientA@email", "password");
        User recipientB = createUser("recipientB@email", "password");
        User senderC = createUser("senderC@email", "password");
        User senderD = createUser("senderD@email", "password");
        userRepository.save(recipientA);
        userRepository.save(recipientB);
        userRepository.save(senderC);
        userRepository.save(senderD);

        ItemBoard itemBoardByA = createItemBoard("인형", 100, recipientA);
        ItemBoard itemBoardByB = createItemBoard("강아지 집", 1000, recipientB);
        itemBoardRepository.save(itemBoardByA);
        itemBoardRepository.save(itemBoardByB);

        //메시지 룸 생성
        MessageRoom roomAC = createMessageRoom(recipientA, senderC, itemBoardByA);
        MessageRoom roomAD = createMessageRoom(recipientA, senderD, itemBoardByA);
        MessageRoom roomBC = createMessageRoom(recipientB, senderC, itemBoardByB);

        messageRoomRepository.save(roomAC);
        messageRoomRepository.save(roomAD);
        messageRoomRepository.save(roomBC);

        //메시지 생성
        Message messageCToA = createMessage(senderC, roomAC);
        Message messageAToC = createMessage(recipientA, roomAC);
        Message messageCToB = createMessage(senderC, roomBC);
        Message messageDToA = createMessage(senderD, roomAD);
        messageRepository.save(messageCToA);
        messageRepository.save(messageAToC);
        messageRepository.save(messageCToB);
        messageRepository.save(messageDToA);

//        //when
        List<MessageRoom> roomForC = messageRoomRepository.findAllByUserId(senderC.getId());
        List<MessageRoom> roomForD = messageRoomRepository.findAllByUserId(senderD.getId());
        List<Message> messageAAndC = messageRepository.findByRoomId(roomAC.getId());
        List<Message> messageAAndD = messageRepository.findByRoomId(roomAD.getId());
        List<Message> messageBAndC = messageRepository.findByRoomId(roomBC.getId());

        //then
        //룸
        assertThat(roomForC.size()).isEqualTo(2);
        assertThat(roomForD.size()).isEqualTo(1);

        assertThat(roomForC.get(0).getSender()).isEqualTo(senderC);

        //메시지
        assertThat(messageAAndC.size()).isEqualTo(2);
        assertThat(messageAAndD.size()).isEqualTo(1);
        assertThat(messageBAndC.size()).isEqualTo(1);
    }

    @Test
    void findWithRefers() {
        //given
        User recipientA = createUser("recipientA@email", "password");
        User senderC = createUser("senderC@email", "password");
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
        MessageRoom findRoom = messageRoomRepository.findWithRefers(roomAC.getId());

        //then
        assertThat(findRoom).isNotNull();
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

    private static User createUser(String emailId, String password) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        return user;
    }
}