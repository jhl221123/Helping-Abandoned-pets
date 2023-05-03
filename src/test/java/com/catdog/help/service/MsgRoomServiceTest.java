package com.catdog.help.service;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MsgRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.ItemRepository;
import com.catdog.help.repository.MsgRoomRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.message.ReadMsgRoomForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MsgRoomServiceTest {

    @InjectMocks
    MsgRoomService msgRoomService;

    @Mock
    MsgRoomRepository msgRoomRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;


    @Test
    @DisplayName("메시지 룸 저장")
    void saveMsgRoom() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");

        Item board = getItem(recipient, "나눔상품");

        doReturn(Optional.ofNullable(board)).when(itemRepository)
                .findById(board.getId());

        doReturn(Optional.ofNullable(sender)).when(userRepository)
                .findByNickname(sender.getNickname());

        doReturn(Optional.ofNullable(recipient)).when(userRepository)
                .findByNickname(recipient.getNickname());

        when(msgRoomRepository.save(any(MsgRoom.class))).then(AdditionalAnswers.returnsFirstArg());

        //expected
        Long roomId = msgRoomService.save(board.getId(), sender.getNickname(), recipient.getNickname());

        verify(itemRepository, times(1)).findById(board.getId());
        verify(userRepository, times(1)).findByNickname(sender.getNickname());
        verify(userRepository, times(1)).findByNickname(recipient.getNickname());
        verify(msgRoomRepository, times(1)).save(any(MsgRoom.class));
    }

    @Test
    @DisplayName("해당 나눔글에서 쪽지를 보낸 적이 있는 경우 메시지 룸 아이디를 반환")
    void existRoom() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");

        Item board = getItem(recipient, "나눔상품");

        MsgRoom room = getMessageRoom(board, sender, recipient);

        doReturn(Optional.ofNullable(sender)).when(userRepository)
                .findByNickname(sender.getNickname());

        doReturn(Optional.ofNullable(room)).when(msgRoomRepository)
                .findByRefers(sender.getId(), board.getId());

        //when
        Long result = msgRoomService.checkRoom(board.getId(), sender.getNickname());// TODO: 2023-04-27 로직을 수정하든, 다시 돌아온다.

        //then
        assertThat(result).isEqualTo(room.getId());

        //verify
        verify(userRepository, times(1)).findByNickname(sender.getNickname());
        verify(msgRoomRepository, times(1)).findByRefers(sender.getId(), board.getId());
    }

    @Test
    @DisplayName("해당 나눔글에서 쪽지를 보낸 적이 없는 경우 -1L을 반환")
    void notExistRoom() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");

        Item board = getItem(recipient, "나눔상품");

        MsgRoom room = getMessageRoom(board, sender, recipient);

        doReturn(Optional.ofNullable(sender)).when(userRepository)
                .findByNickname(sender.getNickname());

        doReturn(Optional.empty()).when(msgRoomRepository)
                .findByRefers(sender.getId(), 3L);

        //when
        Long result = msgRoomService.checkRoom(3L, sender.getNickname());

        //then
        assertThat(result).isEqualTo(-1L);

        //verify
        verify(userRepository, times(1)).findByNickname(sender.getNickname());
        verify(msgRoomRepository, times(1)).findByRefers(sender.getId(), 3L);
    }

    @Test
    @DisplayName("메시지 룸 조회 성공")
    void readRoom() {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");

        Item board = getItem(recipient, "나눔상품");
        setImage(board);

        MsgRoom room = getMessageRoom(board, sender, recipient);

        doReturn(Optional.ofNullable(room)).when(msgRoomRepository)
                .findWithRefers(room.getId());

        //when
        ReadMsgRoomForm form = msgRoomService.read(room.getId());

        //then
        assertThat(form.getItemName()).isEqualTo("테스트상품");
    }


    private void setImage(Item board) {
        UploadFile image = UploadFile.builder()
                .storeFileName("저장이름")
                .uploadFileName("업로드이름")
                .build();
        image.addBoard(board);
    }

    private Message getMessage(User sender, MsgRoom room) {
        return Message.builder()
                .sender(sender)
                .msgRoom(room)
                .content("메시지내용")
                .build();
    }

    private MsgRoom getMessageRoom(Item board, User sender, User recipient) {
        return MsgRoom.builder()
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