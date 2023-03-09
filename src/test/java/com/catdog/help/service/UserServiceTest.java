package com.catdog.help.service;

import com.catdog.help.domain.Gender;
import com.catdog.help.domain.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.SaveUserForm;
import com.catdog.help.web.form.UpdateUserForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


//    @Test
//    void join() {
//        //given
//        SaveUserForm form = new SaveUserForm();
//        doReturn(new Long(1L)).when(userRepository).save(new User());
//
//        //when
//        userServiceImpl.join(form);
//
//        //then
//
//
//    }

    @Test
    void getUserDtoByNickName() {
        //given
        String nickName = "nickName";
        User user = new User();
        user.setNickName("nickName");
        doReturn(user).when(userRepository).findByNickName(nickName);

        //when
        UserDto userDto = userServiceImpl.getUserDtoByNickName(nickName);

        //then
        assertThat(userDto.getNickName()).isEqualTo(nickName);
    }

    @Test
    void getUpdateForm() {
        //given
        String nickName = "nickName";
        User user = new User();
        user.setNickName(nickName);
        doReturn(user).when(userRepository).findByNickName(nickName);

        //when
        UpdateUserForm updateForm = userServiceImpl.getUpdateForm(nickName);

        //then
        assertThat(updateForm.getNickName()).isEqualTo(nickName);

    }

    @Test
    void updateUserInfo() {
        //given
        UpdateUserForm updateForm = getUpdateUserForm("nickName", "name", 20, "man");
        User user = new User();
        user.setId(1L);
        doReturn(user).when(userRepository).findByNickName(updateForm.getNickName());

        //when
        Long userId = userServiceImpl.updateUserInfo(updateForm);

        //then
        assertThat(userId).isEqualTo(user.getId());

    }

    /**========================= private method ========================================*/

    private SaveUserForm createSaveUserForm(String emailId, String nickName) {
        SaveUserForm form = new SaveUserForm();
        form.setEmailId(emailId);
        form.setNickName(nickName);
        form.setPassword("password");
        form.setName("name");
        form.setAge(20);
        form.setGender("gender");
        return form;
    }

    private static UpdateUserForm getUpdateUserForm(String nickName, String name, int age, String gender) {
        UpdateUserForm updateForm = new UpdateUserForm();
        updateForm.setNickName(nickName);
        updateForm.setName(name);
        updateForm.setAge(age);
        updateForm.setGender(gender);
        return updateForm;
    }


}