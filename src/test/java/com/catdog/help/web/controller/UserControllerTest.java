package com.catdog.help.web.controller;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    @DisplayName("회원가입 양식 요청")
    void joinForm() throws Exception {
        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/users/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/joinForm"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("회원가입")
    void join() throws Exception {
        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "id@email")
                        .param("password", "12345678")
                        .param("nickname", "닉네임")
                        .param("name", "이름")
                        .param("age", String.valueOf(20))
                        .param("gender", String.valueOf(Gender.MAN))

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
                .andDo(MockMvcResultHandlers.print());

        //then
        assertThat(userRepository.count()).isEqualTo(1L);

        User findUser = userRepository.findByEmailId("id@email").orElse(null);
        assertThat(findUser.getPassword()).isEqualTo("12345678");
        assertThat(findUser.getNickname()).isEqualTo("닉네임");
        assertThat(findUser.getName()).isEqualTo("이름");
        assertThat(findUser.getAge()).isEqualTo(20);
        assertThat(findUser.getGender()).isEqualTo(Gender.MAN);
    }

    @Test
    @DisplayName("유효하지 않은 이메일 형식으로 회원가입 실패")
    void failJoin_1() throws Exception {
        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "id")
                        .param("password", "12345678")
                        .param("nickname", "닉네임")
                        .param("name", "이름")
                        .param("age", String.valueOf(20))
                        .param("gender", String.valueOf(Gender.MAN))

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/joinForm"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("saveUserForm", "emailId"))
                .andDo(MockMvcResultHandlers.print());

        //then
        assertThat(userRepository.count()).isEqualTo(0L);
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호로 회원가입 실패")
    void failJoin_2() throws Exception {
        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "id@email")
                        .param("password", "12345")
                        .param("nickname", "닉네임")
                        .param("name", "이름")
                        .param("age", String.valueOf(20))
                        .param("gender", String.valueOf(Gender.MAN))

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/joinForm"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("saveUserForm", "password"))
                .andDo(MockMvcResultHandlers.print());

        //then
        assertThat(userRepository.count()).isEqualTo(0L);
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패")
    void failJoin_3() throws Exception {
        //given
        User user = User.builder()
                .emailId("same@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "same@email")
                        .param("password", "12345678")
                        .param("nickname", "다른닉네임")
                        .param("name", "이름")
                        .param("age", String.valueOf(20))
                        .param("gender", String.valueOf(Gender.MAN))

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/joinForm"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("saveUserForm", "emailId"))
                .andDo(MockMvcResultHandlers.print());

        //then
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입 실패")
    void failJoin_4() throws Exception {
        //given
        User user = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임중복")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "differ@email")
                        .param("password", "12345678")
                        .param("nickname", "닉네임중복")
                        .param("name", "이름")
                        .param("age", String.valueOf(20))
                        .param("gender", String.valueOf(Gender.MAN))

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/joinForm"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("saveUserForm", "nickname"))
                .andDo(MockMvcResultHandlers.print());

        //then
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인 양식 요청")
    void loginForm() throws Exception {
        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/loginForm"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        //given
        User user = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "id@email")
                        .param("password", "12345678")

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유효하지 않은 이메일 형식으로 로그인 실패")
    void failLogin_1() throws Exception {
        //given
        User user = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "id")
                        .param("password", "12345678")

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/loginForm"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("loginForm", "emailId"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호로 로그인 실패")
    void failLogin_2() throws Exception {
        //given
        User user = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "id@email")
                        .param("password", "12345")

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/loginForm"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrors("loginForm", "password"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 실패")
    void failLogin_3() throws Exception {
        //given
        User user = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "differ@email")
                        .param("password", "12345678")

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/loginForm"))
                .andExpect(MockMvcResultMatchers.model().attributeErrorCount("loginForm", 1))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("비밀번호 불일치로 로그인 실패")
    void failLogin_4() throws Exception {
        //given
        User user = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(user);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailId", "id@email")
                        .param("password", "12345678999")

                )
                .andExpect(MockMvcResultMatchers.handler().handlerType(UserController.class))
                .andExpect(MockMvcResultMatchers.view().name("users/loginForm"))
                .andExpect(MockMvcResultMatchers.model().attributeErrorCount("loginForm", 1))
                .andDo(MockMvcResultHandlers.print());
    }

}