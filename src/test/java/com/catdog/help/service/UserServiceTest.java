package com.catdog.help.service;

import com.catdog.help.MyConst;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.user.SaveUserForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired JpaUserRepository userRepository;


    @Test
    @DisplayName("회원가입")
    void join() {
        //given
        SaveUserForm form = SaveUserForm.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();

        //when
        Long id = userService.join(form);
        User user = userRepository.findById(id);

        //then
        assertThat(user.getEmailId()).isEqualTo("id@email");
        assertThat(user.getPassword()).isEqualTo("12345678");
        assertThat(user.getNickname()).isEqualTo("닉네임");
    }

    @Test
    @DisplayName("이메일 아이디 중복 확인")
    void checkEmail() {
        //given
        SaveUserForm form = SaveUserForm.builder()
                .emailId("same@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userService.join(form);

        //expected
        assertThat(userService.isEmailDuplication("differ@email")).isFalse();
        assertThat(userService.isEmailDuplication("same@email")).isTrue();

    }

    @Test
    @DisplayName("닉네임 중복 확인")
    void checkNickName() {
        //given
        SaveUserForm form = SaveUserForm.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("중복닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userService.join(form);

        //expected
        assertThat(userService.isNicknameDuplication("다른닉네임")).isFalse();
        assertThat(userService.isNicknameDuplication("중복닉네임")).isTrue();

    }

    @Test
    @DisplayName("로그인")
    void login() {
        //given
        SaveUserForm form = SaveUserForm.builder()
                .emailId("user@email")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();

        userService.join(form);

        //when
        String nickname = userService.login("user@email", "12345678");//정상 로그인
        String failById = userService.login("no@email", "12345678");//존재하지 않는 아이디
        String failByPassword = userService.login("user@email", "12345678999");//비밀번호 불일치

        //then
        assertThat(nickname).isEqualTo("닉네임");
        assertThat(failById).isEqualTo(MyConst.FAIL_LOGIN);
        assertThat(failByPassword).isEqualTo(MyConst.FAIL_LOGIN);
    }
}