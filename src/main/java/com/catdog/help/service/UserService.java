package com.catdog.help.service;

import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.user.ChangePasswordForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;

public interface UserService {
    public Long join(SaveUserForm saveUserForm);

    public boolean checkEmailDuplication(String email);

    public boolean checkNickNameDuplication(String nickName);

    public UserDto login(String emailId, String password);

    public UserDto getUserDtoByNickName(String nickName);

    public UpdateUserForm getUpdateForm(String nickName);

    public Long updateUserInfo(UpdateUserForm updateForm);

    public Long changePassword(ChangePasswordForm changeForm, String nickName);

    public void deleteUser(String nickName);
}
