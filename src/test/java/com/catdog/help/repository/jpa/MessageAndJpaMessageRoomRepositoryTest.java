package com.catdog.help.repository.jpa;

import com.catdog.help.TestData;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.message.SaveMessageForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MessageAndJpaMessageRoomRepositoryTest {

    @Autowired JpaMessageRoomRepository jpaMessageRoomRepository;
    @Autowired JpaMessageRepository jpaMessageRepository;
    @Autowired JpaItemBoardRepository itemBoardRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired TestData testData;

    @Test
    void 저장_조회() {
        //given
        User recipientA = testData.createUser("recipientA@email", "password", "recipientA");
        User recipientB = testData.createUser("recipientB@email", "password", "recipientB");
        User senderC = testData.createUser("senderC@email", "password", "senderC");
        User senderD = testData.createUser("senderD@email", "password", "senderD");
        userRepository.save(recipientA);
        userRepository.save(recipientB);
        userRepository.save(senderC);
        userRepository.save(senderD);

        ItemBoard itemBoardByA = testData.createItemBoard("인형", 100, recipientA);
        ItemBoard itemBoardByB = testData.createItemBoard("강아지 집", 1000, recipientB);
        itemBoardRepository.save(itemBoardByA);
        itemBoardRepository.save(itemBoardByB);

        //메시지 룸 생성
        MessageRoom roomAC = testData.createMessageRoom(recipientA, senderC, itemBoardByA);
        MessageRoom roomAD = testData.createMessageRoom(recipientA, senderD, itemBoardByA);
        MessageRoom roomBC = testData.createMessageRoom(recipientB, senderC, itemBoardByB);

        jpaMessageRoomRepository.save(roomAC);
        jpaMessageRoomRepository.save(roomAD);
        jpaMessageRoomRepository.save(roomBC);

        //메시지 생성
        Message messageCToA = testData.createMessage(senderC, roomAC);
        Message messageAToC = testData.createMessage(recipientA, roomAC);
        Message messageCToB = testData.createMessage(senderC, roomBC);
        Message messageDToA = testData.createMessage(senderD, roomAD);
        jpaMessageRepository.save(messageCToA);
        jpaMessageRepository.save(messageAToC);
        jpaMessageRepository.save(messageCToB);
        jpaMessageRepository.save(messageDToA);

//        //when
        List<MessageRoom> roomForC = jpaMessageRoomRepository.findAllByUserId(senderC.getId());
        List<MessageRoom> roomForD = jpaMessageRoomRepository.findAllByUserId(senderD.getId());
        List<Message> messageAAndC = jpaMessageRepository.findByRoomId(roomAC.getId());
        List<Message> messageAAndD = jpaMessageRepository.findByRoomId(roomAD.getId());
        List<Message> messageBAndC = jpaMessageRepository.findByRoomId(roomBC.getId());

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
        User recipientA = testData.createUser("recipientA@email", "password", "recipientA");
        User senderC = testData.createUser("senderC@email", "password", "senderC");
        userRepository.save(recipientA);
        userRepository.save(senderC);

        ItemBoard itemBoardByA = testData.createItemBoard("인형", 100, recipientA);
        itemBoardRepository.save(itemBoardByA);

        MessageRoom roomAC = testData.createMessageRoom(recipientA, senderC, itemBoardByA);
        jpaMessageRoomRepository.save(roomAC);

        Message messageCToA = testData.createMessage(senderC, roomAC);
        Message messageAToC = testData.createMessage(recipientA, roomAC);
        jpaMessageRepository.save(messageCToA);
        jpaMessageRepository.save(messageAToC);

        //when
        jpaMessageRoomRepository.flushAndClear();
        MessageRoom findRoom = jpaMessageRoomRepository.findWithRefers(roomAC.getId());

        //then
        assertThat(findRoom).isNotNull();
    }
}