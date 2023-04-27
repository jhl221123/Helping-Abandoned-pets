package com.catdog.help.repository;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MessageRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;


    @Test
    @DisplayName("메시지 저장")
    void saveMessage() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");
        userRepository.save(sender);
        userRepository.save(recipient);

        Item board = getItem(recipient, "나눔제목");
        itemRepository.save(board);

        MessageRoom room = MessageRoom.builder()
                .item(board)
                .sender(sender)
                .recipient(recipient)
                .build();

        Message message = Message.builder()
                .messageRoom(room)
                .sender(sender)
                .content("발신자의 메시지")
                .build();

        //when
        Message savedMessage = messageRepository.save(message);

        //then
        assertThat(savedMessage.getContent()).isEqualTo(message.getContent());
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