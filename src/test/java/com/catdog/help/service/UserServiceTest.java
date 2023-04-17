package com.catdog.help.service;

import com.catdog.help.TestData;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired JpaUserRepository userRepository;
    @Autowired TestData testData;


    @Test
    void join() {
        //given
        SaveUserForm form = testData.getSaveUserForm("user@123", "nickName", "12345");

        //when
        Long userId = userService.join(form);
        User findUser = userRepository.findById(userId);

        //then
        assertThat(userId).isInstanceOf(Long.class);
        assertThat(findUser.getEmailId()).isEqualTo(form.getEmailId());
    }

    @Test
    void checkDuplication() {
        //given
        userService.join(testData.getSaveUserForm("user@email", "nickName", "password"));

        //when
        boolean duplicatedEmailId = userService.checkEmailDuplication("user@email");
        boolean availableEmailId = userService.checkEmailDuplication("new@email");
        boolean duplicatedNickName = userService.checkNicknameDuplication("nickName");
        boolean availableNickName = userService.checkNicknameDuplication("newNickName");

        //then
        assertThat(duplicatedEmailId).isEqualTo(true);
        assertThat(availableEmailId).isEqualTo(false);
        assertThat(duplicatedNickName).isEqualTo(true);
        assertThat(availableNickName).isEqualTo(false);
    }

    @Test
    void login() {
        //given
        Long userId = userService.join(testData.getSaveUserForm("user@email", "password", "nickName"));

        //when
        ReadUserForm loginReadUserForm1 = userService.login("user@email", "password");    //정상 로그인
        ReadUserForm loginReadUserForm2 = userService.login("no@email", "password");      //존재하지 않는 아이디
        ReadUserForm loginReadUserForm3 = userService.login("user@email", "noPassword"); //비밀번호 불일치

        //then
        assertThat(loginReadUserForm1.getId()).isEqualTo(userId);
        assertThat(loginReadUserForm2).isNull();
        assertThat(loginReadUserForm3).isNull();
    }

    @Test
    void getUserDtoByNickName() {
        //given
        userService.join(testData.getSaveUserForm("user@email", "nickName", "password"));
        String nickName = "nickName";
        String nonexistentNickName = "nonexistentNickName";

        //when
        ReadUserForm readUserForm = userService.readByNickname(nickName);
        ReadUserForm nullDto = userService.readByNickname(nonexistentNickName);

        //then
        assertThat(readUserForm.getEmailId()).isEqualTo("user@email");
        assertThat(readUserForm.getNickname()).isEqualTo(nickName);
        assertThat(nullDto).isNull();
    }

    @Test
    void getUpdateForm() {
        //given
        userService.join(testData.getSaveUserForm("user@email", "nickName", "password"));
        String nickName = "nickName";
        String nonexistentNickName = "nonexistentNickName";

        //when
        UpdateUserForm form = userService.getUpdateForm(nickName);
        UpdateUserForm nullForm = userService.getUpdateForm(nonexistentNickName);

        //then
        assertThat(form.getNickname()).isEqualTo(nickName);
        assertThat(nullForm).isNull();
    }

    @Test
    void updateUserInfo() {
        //given
        userService.join(testData.getSaveUserForm("user@email", "nickName", "password"));
        UpdateUserForm form = new UpdateUserForm();
        form.setNickname("nickName");
        form.setName("newName");
        form.setAge(10);
        form.setGender(Gender.WOMAN);

        //when
        Long userId = userService.updateUserInfo(form);
        User findUser = userRepository.findById(userId);

        //then
        assertThat(findUser.getName()).isEqualTo("newName");
        assertThat(findUser.getAge()).isEqualTo(10);
        assertThat(findUser.getGender()).isEqualTo(Gender.WOMAN);
    }
}