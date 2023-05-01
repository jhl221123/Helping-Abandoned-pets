package com.catdog.help.web.controller;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.message.Message;
import com.catdog.help.domain.message.MsgRoom;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.MessageService;
import com.catdog.help.service.MsgRoomService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.message.ReadMessageForm;
import com.catdog.help.web.form.message.ReadMsgRoomForm;
import com.catdog.help.web.form.message.SaveMessageForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @InjectMocks
    MessageController messageController;

    @Mock
    MessageService messageService;

    @Mock
    MsgRoomService msgRoomService;

    private MockMvc mockMvc;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }


    @Test
    @DisplayName("이미 메시지 룸이 존재해서 해당 룸으로 이동")
    void getExistRoom() throws Exception {
        //given
        doReturn(4L).when(msgRoomService)
                .checkRoom(3L, "발신자");

        //expected
        mockMvc.perform(get("/messages/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "발신자")
                        .param("boardId", String.valueOf(3L))
                        .param("recipientNick", "수신자")
                )
                .andExpect(redirectedUrl("/messages/" + 4L));
    }

    @Test
    @DisplayName("메시지 룸이 존재하지 않아 새로 생성")
    void createNewRoom() throws Exception {
        //given
        doReturn(-1L).when(msgRoomService)
                .checkRoom(3L, "발신자");

        doReturn(4L).when(msgRoomService)
                .save(3L, "발신자", "수신자");

        //expected
        mockMvc.perform(get("/messages/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "발신자")
                        .param("boardId", String.valueOf(3L))
                        .param("recipientNick", "수신자")
                )
                .andExpect(redirectedUrl("/messages/" + 4L));
    }

    @Test
    @DisplayName("메시지 룸 조회 성공")
    void getRoom() throws Exception {
        //given
        User sender = getUser("sender@test.test", "발신자");
        User recipient = getUser("recipient@test.test", "수신자");

        Item board = getItem(recipient, "나눔상품");

        MsgRoom room = getMessageRoom(sender, recipient, board);

        Message message = getMessage(sender, room);
        List<ReadMessageForm> messages = getReadMessageForms(message);

        ReadMsgRoomForm form = ReadMsgRoomForm.builder()
                .msgRoom(room)
                .messageForms(messages)
                .build();

        doReturn(form).when(msgRoomService)
                .read(4L);

        //expected
        mockMvc.perform(get("/messages/{roomId}", 4L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "발신자")
                )
                .andExpect(view().name("messages/room"));
    }

    @Test
    @DisplayName("메시지 보내기 성공")
    void sendMessage() throws Exception {
        //given
        User sender = getUser("sender@test.test", "발신자");

        doNothing().when(messageService)
                .save(eq(4L), eq(sender.getNickname()), any(SaveMessageForm.class));

        //expected
        mockMvc.perform(post("/messages/{roomId}", 4L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "발신자")
                        .param("content", "메시지내용")
                )
                .andExpect(redirectedUrl("/messages/" + 4L));
    }

    @Test
    @DisplayName("해당 사용자가 속한 메시지 룸 페이지로 조회")
    void getPage() throws Exception {
        //given

        //when

    }


    private List<ReadMessageForm> getReadMessageForms(Message message) {
        ReadMessageForm messageForm = new ReadMessageForm(message);
        List<ReadMessageForm> messages = new ArrayList<>();
        messages.add(messageForm);
        return messages;
    }


    private Message getMessage(User sender, MsgRoom room) {
        return Message.builder()
                .sender(sender)
                .msgRoom(room)
                .content("메시지내용")
                .build();
    }

    private MsgRoom getMessageRoom(User sender, User recipient, Item board) {
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