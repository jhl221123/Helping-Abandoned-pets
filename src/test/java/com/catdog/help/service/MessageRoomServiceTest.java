package com.catdog.help.service;

import com.catdog.help.TestData;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.JpaMessageRepository;
import com.catdog.help.repository.jpa.JpaMessageRoomRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.message.ReadMessageRoomForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class MessageRoomServiceTest {

    @Autowired MessageRoomService messageRoomService;
    @Autowired JpaMessageRoomRepository jpaMessageRoomRepository;
    @Autowired JpaMessageRepository jpaMessageRepository;
    @Autowired JpaItemBoardRepository itemBoardRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired TestData testData;

    @Test
    void readRoom() {
        //given
        User recipientA = testData.createUser("recipientA@email", "password", "nickName1");
        User senderC = testData.createUser("senderC@email", "password", "nickName2");
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
        ReadMessageRoomForm roomFrom = messageRoomService.readRoom(roomAC.getId());

        //then
        System.out.println("roomFrom = " + roomFrom);
        Assertions.assertThat(roomFrom).isNotNull();
    }


}