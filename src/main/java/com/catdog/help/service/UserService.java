package com.catdog.help.service;

import com.catdog.help.domain.User;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.SaveUserForm;
import com.catdog.help.web.form.UpdateUserForm;

public interface UserService {
    public Long join(SaveUserForm saveUserForm);

    public boolean checkEmailDuplication(String email);

    public boolean checkNickNameDuplication(String nickName);

    public UserDto login(String emailId, String password);

    public UserDto getUserDtoByNickName(String nickName);

    public UpdateUserForm getUpdateForm(String nickName);

    public Long updateUserInfo(UpdateUserForm updateForm);
}
