package com.catdog.help.web.api.controller;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.api.response.user.SaveUserResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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