package com.catdog.help.repository;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MsgRoomRepositoryTest {

    @Autowired
    MsgRoomRepository msgRoomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;


    @Test
    @DisplayName("메시지 룸 저장")
    void saveRoom() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");
        userRepository.save(sender);
        userRepository.save(recipient);

        Item board = getItem(recipient, "나눔제목");
        itemRepository.save(board);

        MessageRoom room = getMessageRoom(board, sender, recipient);

        //when
        MessageRoom savedRoom = msgRoomRepository.save(room);

        //then
        assertThat(savedRoom).isEqualTo(room);
    }

    private static MessageRoom getMessageRoom(Item board, User sender, User recipient) {
        MessageRoom room = MessageRoom.builder()
                .item(board)
                .sender(sender)
                .recipient(recipient)
                .build();
        return room;
    }

    @Test
    @DisplayName("메시지 룸 단건 조회")
    void findById() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");
        userRepository.save(sender);
        userRepository.save(recipient);

        Item board = getItem(recipient, "나눔제목");
        itemRepository.save(board);

        MessageRoom room = getMessageRoom(board, sender, recipient);
        msgRoomRepository.save(room);

        //when
        MessageRoom findRoom = msgRoomRepository.findById(room.getId()).get();

        //then
        assertThat(findRoom).isEqualTo(room);
    }

    @Test
    @DisplayName("페치 조인을 사용해서 메시지 룸 조회 시 참조 값을 한 번에 조회")
    void findWithRefers() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");
        userRepository.save(sender);
        userRepository.save(recipient);

        Item board = getItem(recipient, "나눔제목");
        itemRepository.save(board);

        MessageRoom room = getMessageRoom(board, sender, recipient);
        msgRoomRepository.save(room);

        //when
        MessageRoom findRoom = msgRoomRepository.findWithRefers(room.getId()).get();

        //then
        assertThat(findRoom).isEqualTo(room);
    }

    @Test
    @DisplayName("해당 유저가 속한 메시지 룸을 모두 조회")
    void findByUser() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");
        userRepository.save(sender);
        userRepository.save(recipient);

        Item boardBySender = getItem(sender, "발신자 작성글");
        Item boardByRecipient = getItem(recipient, "수신자 작성글");
        itemRepository.save(boardBySender);
        itemRepository.save(boardByRecipient);

        MessageRoom roomA = getMessageRoom(boardByRecipient, sender, recipient);
        MessageRoom roomB = getMessageRoom(boardBySender, recipient, sender);
        msgRoomRepository.save(roomA);
        msgRoomRepository.save(roomB);

        //when
        List<MessageRoom> rooms = msgRoomRepository.findAllByUser(sender.getId());

        //then
        assertThat(rooms.size()).isEqualTo(2);
    }

//    @Test
//    @DisplayName("해당 유저의 메시지 룸 페이지 조회")
//    void findPage() {
//        //given
//        User sender = getUser("sender@test.test", "발신자");
//        User recipient = getUser("recipient@test.test", "수신자");
//        userRepository.save(sender);
//        userRepository.save(recipient);
//
//        Item boardBySender = getItem(sender, "발신자 작성글");
//        Item boardByRecipient = getItem(recipient, "수신자 작성글");
//        itemRepository.save(boardBySender);
//        itemRepository.save(boardByRecipient);
//
//        MessageRoom roomA = getMessageRoom(boardByRecipient, sender, recipient);
//        MessageRoom roomB = getMessageRoom(boardBySender, recipient, sender);
//        msgRoomRepository.save(roomA);
//        msgRoomRepository.save(roomB);
//
//        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");
//
//        //when
//        Page<MessageRoom> page = msgRoomRepository.findPageBySenderIdOrRecipientId(sender.getId(), pageRequest);
//
//        //then
//        assertThat(page.getContent().size()).isEqualTo(2L);
//    }

    @Test
    @DisplayName("해당 유저가 속한 메시지 룸 개수를 조회")
    void countByUser() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");
        userRepository.save(sender);
        userRepository.save(recipient);

        Item boardBySender = getItem(sender, "발신자 작성글");
        Item boardByRecipient = getItem(recipient, "수신자 작성글");
        itemRepository.save(boardBySender);
        itemRepository.save(boardByRecipient);

        MessageRoom roomA = getMessageRoom(boardByRecipient, sender, recipient);
        MessageRoom roomB = getMessageRoom(boardBySender, recipient, sender);
        msgRoomRepository.save(roomA);
        msgRoomRepository.save(roomB);

        //when
        Long result = msgRoomRepository.countByUser(sender.getId());

        //then
        assertThat(result).isEqualTo(2L);
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