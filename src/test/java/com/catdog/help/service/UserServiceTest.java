package com.catdog.help.service;

import com.catdog.help.MyConst;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.NotFoundUserException;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.user.ChangePasswordForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;


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
        User user = userRepository.findById(id).get();

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

    @Test
    @DisplayName("관리자 계정 확인")
    void isManager() {
        //given
        User manager = User.builder()
                .emailId("manager@email")
                .password("12345678")
                .nickname("매니저")
                .name("관리자")
                .age(20)
                .gender(Gender.MAN)
                .build();
        manager.makeManager();
        userRepository.save(manager);

        User basicUser = User.builder()
                .emailId("basic@email")
                .password("12345678")
                .nickname("일반사용자")
                .name("사용자")
                .age(20)
                .gender(Gender.MAN)
                .build();
        userRepository.save(basicUser);

        //expected
        assertThrows(NotFoundUserException.class,
                () -> userService.isManager("존재하지않는사용자"));

        Boolean basicResult = userService.isManager("일반사용자");
        assertThat(basicResult).isFalse();

        Boolean managerResult = userService.isManager("매니저");
        assertThat(managerResult).isTrue();
    }

    @Test
    @DisplayName("ReadUserForm 조회")
    void readByNickname() {
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
        assertThrows(NotFoundUserException.class,
                () -> userService.readByNickname("존재하지않는사용자"));

        ReadUserForm readForm = userService.readByNickname("닉네임");
        assertThat(readForm.getEmailId()).isEqualTo("id@email");
        assertThat(readForm.getPassword()).isEqualTo("12345678");
        assertThat(readForm.getName()).isEqualTo("이름");
        assertThat(readForm.getAge()).isEqualTo(20);
        assertThat(readForm.getGender()).isEqualTo(Gender.MAN);
    }

    @Test
    @DisplayName("UpdateUserForm 호출")
    void getUpdateForm() {
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
        assertThrows(NotFoundUserException.class,
                () -> userService.getUpdateForm("존재하지않는사용자"));

        UpdateUserForm updateForm = userService.getUpdateForm("닉네임");
        assertThat(updateForm.getName()).isEqualTo("이름");
        assertThat(updateForm.getAge()).isEqualTo(20);
        assertThat(updateForm.getGender()).isEqualTo(Gender.MAN);
    }

    @Test
    @DisplayName("개인정보 변경")
    void updateInfo() {
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

        User updatedUser = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("닉네임")
                .name("변경된이름")
                .age(30)
                .gender(Gender.WOMAN)
                .build();
        UpdateUserForm UpdatedForm = new UpdateUserForm(updatedUser);

        User nonexistent = User.builder()
                .emailId("id@email")
                .password("12345678")
                .nickname("존재하지않는사용자")
                .name("변경된이름")
                .age(30)
                .gender(Gender.WOMAN)
                .build();
        UpdateUserForm exceptionForm = new UpdateUserForm(nonexistent);

        //expected
        assertThrows(NotFoundUserException.class,
                () -> userService.updateUserInfo(exceptionForm));

        Long id = userService.updateUserInfo(UpdatedForm);
        User findUser = userRepository.findByNickname("닉네임").get();
        assertThat(findUser.getName()).isEqualTo(updatedUser.getName());
        assertThat(findUser.getAge()).isEqualTo(updatedUser.getAge());
        assertThat(findUser.getGender()).isEqualTo(updatedUser.getGender());
    }

    @Test
    @DisplayName("비밀번호 확인")
    void validPassword() {
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

        //when
        Boolean result = userService.isSamePassword("12345678", "닉네임");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {
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

        ChangePasswordForm success = ChangePasswordForm.builder()
                .beforePassword("12345678")
                .afterPassword("87654321")
                .checkPassword("87654321")
                .build();

        //when
        userService.changePassword(success, "닉네임");

        //then
        User findUser = userRepository.findByNickname("닉네임").get();
        assertThat(findUser.getPassword()).isEqualTo("87654321");
    }

    @Test
    @DisplayName("회원 탈퇴")
    void delete() {
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
        assertThat(userRepository.count()).isEqualTo(1L);

        //when
        userService.deleteUser("닉네임");

        //then
        Optional<User> result = userRepository.findByNickname("닉네임");
        assertThat(result).isEmpty();
        assertThat(userRepository.count()).isEqualTo(0L);
    }
}