package com.catdog.help.web.api.controller;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.EmailDuplicateException;
import com.catdog.help.exception.NicknameDuplicateException;
import com.catdog.help.exception.PasswordIncorrectException;
import com.catdog.help.exception.PasswordNotSameException;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.api.request.user.ChangePasswordRequest;
import com.catdog.help.web.api.request.user.EditUserRequest;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.api.response.user.SaveUserResponse;
import com.catdog.help.web.form.user.EditUserForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Transactional
class UserApiControllerTest {

    @InjectMocks
    UserApiController userApiController;

    @Mock
    private UserService userService;

    @Mock
    private BulletinService bulletinService;

    @Mock
    private ItemService itemService;

    @Mock
    private InquiryService inquiryService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userApiController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("회원가입 성공")
    void successJoin() throws Exception {
        //given
        SaveUserRequest request = getSaveUserRequest();
        String json = objectMapper.writeValueAsString(request);

        SaveUserResponse response = new SaveUserResponse(1L);
        String result = objectMapper.writeValueAsString(response);

        doReturn(1L).when(userService)
                .join(any(SaveUserForm.class));

        //expected
        mockMvc.perform(post("/api/users/new")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(content().json(result));
    }

    @Test
    @DisplayName("검증으로 인한 회원가입 실패")
    void failJoin() throws Exception {
        //given
        SaveUserRequest request = SaveUserRequest.builder()
                .emailId("")
                .password("")
                .name("")
                .nickname("")
                .age(-1)
                .gender(null)
                .build();
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/api/users/new")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패")
    void failJoinByEmail() throws Exception {
        //given
        SaveUserRequest request = getSaveUserRequest();
        String json = objectMapper.writeValueAsString(request);

        doReturn(true).when(userService)
                .isEmailDuplication("test@test.test");

        //expected
        assertThatThrownBy(() -> mockMvc.perform(post("/api/users/new")
                .contentType(APPLICATION_JSON)
                .content(json)
        )).hasCause(new EmailDuplicateException());
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입 실패")
    void failJoinByNickname() throws Exception {
        //given
        SaveUserRequest request = getSaveUserRequest();
        String json = objectMapper.writeValueAsString(request);

        doReturn(true).when(userService)
                .isNicknameDuplication("닉네임");

        //expected
        assertThatThrownBy(() -> mockMvc.perform(post("/api/users/new")
                .contentType(APPLICATION_JSON)
                .content(json)
        )).hasCause(new NicknameDuplicateException());
    }

    @Test
    @DisplayName("닉네임으로 사용자 정보 조회")
    void readByNickname() throws Exception {
        //given
        User user = getUser();
        ReadUserForm form = new ReadUserForm(user);
        ReadUserResponse response = new ReadUserResponse(form);
        String result = objectMapper.writeValueAsString(response);

        doReturn(form).when(userService)
                .readByNickname(user.getNickname());

        //expected
        mockMvc.perform(get("/api/users/detail")
                        .contentType(APPLICATION_JSON)
                        .param("nickname", "닉네임")
                )
                .andExpect(content().json(result))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void edit() throws Exception {
        //given
        EditUserRequest request = getEditUserRequest();
        String json = objectMapper.writeValueAsString(request);

        doReturn(2L).when(userService)
                .updateUserInfo(any(EditUserForm.class));

        //expected
        mockMvc.perform(post("/api/users/edit")
                .contentType(APPLICATION_JSON)
                .content(json)
        );
        verify(userService, times(1)).updateUserInfo(any(EditUserForm.class));
    }

    @Test
    @DisplayName("회원 비밀번호 변경 성공")
    void successChangePassword() throws Exception {
        //given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .nickname("닉네임")
                .beforePassword("12341234")
                .afterPassword("43214321")
                .checkPassword("43214321")
                .build();

        String json = objectMapper.writeValueAsString(request);

        doReturn(true).when(userService)
                .isSamePassword(request.getBeforePassword(), request.getNickname());

        //expected
        mockMvc.perform(post("/api/users/edit/password")
                .contentType(APPLICATION_JSON)
                .content(json)
        );
        verify(userService, times(1)).changePassword(request.getAfterPassword(), request.getNickname());
    }

    @Test
    @DisplayName("기존 비밀번호 불일치로 비밀번호 변경 실패")
    void failChangePasswordByIncorrectBeforePassword() throws Exception {
        //given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .nickname("닉네임")
                .beforePassword("123412341234")
                .afterPassword("43214321")
                .checkPassword("43214321")
                .build();

        String json = objectMapper.writeValueAsString(request);

        doReturn(false).when(userService)
                .isSamePassword(request.getBeforePassword(), request.getNickname());

        //expected
        assertThatThrownBy(() -> mockMvc.perform(post("/api/users/edit/password")
                .contentType(APPLICATION_JSON)
                .content(json)
        )).hasCause(new PasswordIncorrectException());
    }

    @Test
    @DisplayName("변경하려는 비밀번호 불일치로 비밀번호 변경 실패")
    void failChangePasswordByNotSameAfterPassword() throws Exception {
        //given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .nickname("닉네임")
                .beforePassword("12341234")
                .afterPassword("43214321")
                .checkPassword("432143214321")
                .build();

        String json = objectMapper.writeValueAsString(request);

        doReturn(true).when(userService)
                .isSamePassword(request.getBeforePassword(), request.getNickname());

        //expected
        assertThatThrownBy(() -> mockMvc.perform(post("/api/users/edit/password")
                .contentType(APPLICATION_JSON)
                .content(json)
        )).hasCause(new PasswordNotSameException());
    }


    private EditUserRequest getEditUserRequest() {
        return EditUserRequest.builder()
                .nickname("닉네임")
                .name("수정된_이름")
                .age(33)
                .gender(Gender.WOMAN)
                .build();
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12341234")
                .name("이름")
                .nickname("닉네임")
                .age(22)
                .gender(Gender.MAN)
                .build();
    }

    private SaveUserRequest getSaveUserRequest() {
        return SaveUserRequest.builder()
                .emailId("test@test.test")
                .password("12341234")
                .name("이름")
                .nickname("닉네임")
                .age(22)
                .gender(Gender.MAN)
                .build();
    }
}