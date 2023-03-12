package com.catdog.help.service;

import com.catdog.help.domain.Gender;
import com.catdog.help.domain.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    @Test
    void join() {
        //given
        SaveUserForm form = getSaveUserForm("user@123", "nickName", "12345");

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
        userService.join(getSaveUserForm("user@email", "nickName", "password"));

        //when
        boolean duplicatedEmailId = userService.checkEmailDuplication("user@email");
        boolean availableEmailId = userService.checkEmailDuplication("new@email");
        boolean duplicatedNickName = userService.checkNickNameDuplication("nickName");
        boolean availableNickName = userService.checkNickNameDuplication("newNickName");

        //then
        assertThat(duplicatedEmailId).isEqualTo(true);
        assertThat(availableEmailId).isEqualTo(false);
        assertThat(duplicatedNickName).isEqualTo(true);
        assertThat(availableNickName).isEqualTo(false);
    }

    @Test
    void login() {
        //given
        Long userId = userService.join(getSaveUserForm("user@email", "nickName", "password"));

        //when
        UserDto loginUserDto1 = userService.login("user@email", "password");    //정상 로그인
        UserDto loginUserDto2 = userService.login("no@email", "password");      //존재하지 않는 아이디
        UserDto loginUserDto3 = userService.login("user@email", "noPassword"); //비밀번호 불일치

        //then
        assertThat(loginUserDto1.getId()).isEqualTo(userId);
        assertThat(loginUserDto2).isNull();
        assertThat(loginUserDto3).isNull();
    }

    @Test
    void getUserDtoByNickName() {
        //given
        userService.join(getSaveUserForm("user@email", "nickName", "password"));
        String nickName = "nickName";
        String nonexistentNickName = "nonexistentNickName";

        //when
        UserDto userDto = userService.getUserDtoByNickName(nickName);
        UserDto nullDto = userService.getUserDtoByNickName(nonexistentNickName);

        //then
        assertThat(userDto.getEmailId()).isEqualTo("user@email");
        assertThat(userDto.getNickName()).isEqualTo(nickName);
        assertThat(nullDto).isNull();
    }

    @Test
    void getUpdateForm() {
        //given
        userService.join(getSaveUserForm("user@email", "nickName", "password"));
        String nickName = "nickName";
        String nonexistentNickName = "nonexistentNickName";

        //when
        UpdateUserForm form = userService.getUpdateForm(nickName);
        UpdateUserForm nullForm = userService.getUpdateForm(nonexistentNickName);

        //then
        assertThat(form.getNickName()).isEqualTo(nickName);
        assertThat(nullForm).isNull();
    }

    @Test
    void updateUserInfo() {
        //given
        userService.join(getSaveUserForm("user@email", "nickName", "password"));
        UpdateUserForm form = new UpdateUserForm();
        form.setNickName("nickName");
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


    /**============================= private method ==============================*/

    private SaveUserForm getSaveUserForm(String emailId, String nickName, String password) {
        SaveUserForm form = new SaveUserForm();
        form.setEmailId(emailId);
        form.setPassword(password);
        form.setNickName(nickName);
        form.setName("name");
        form.setAge(20);
        form.setGender(Gender.MAN);
        return form;
    }

    private UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmailId("user@email");
        userDto.setPassword("password");
        userDto.setNickName("nickName");
        userDto.setName("name");
        userDto.setAge(20);
//        userDto.setGender(Gender.MAN);
        userDto.setReliability(1);
        userDto.setJoinDate(LocalDateTime.now());
        return userDto;
    }
}