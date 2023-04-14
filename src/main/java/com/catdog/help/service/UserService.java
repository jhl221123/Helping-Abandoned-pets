package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.Grade;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.user.ChangePasswordForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository userRepository;

    @Transactional
    public Long join(SaveUserForm form) {
        User user = createUser(form);
        userRepository.save(user);
        return user.getId();
    }

    public boolean checkEmailDuplication(String email) {// TODO: 2023-03-08 private으로 수정 후 join에서 처리하도록 수정
        User findUser = userRepository.findByEmailId(email);
        if (findUser == null) {
            return false;
        }
        return true;
    }

    public boolean checkNicknameDuplication(String nickname) {// TODO: 2023-03-08 private으로 수정 후 join에서 처리하도록 수정
        User findUser = userRepository.findByNickname(nickname);
        if (findUser == null) {
            return false;
        }
        return true;
    }

    @Transactional
    public ReadUserForm login(String emailId, String password) {
        User findUser = userRepository.findByEmailId(emailId);
        if (findUser == null || !findUser.getPassword().equals(password)) {
            return null;
        }

        ReadUserForm findReadForm = getReadUserForm(findUser);

        return findReadForm;
    }

    public Boolean isManager(String nickname) {
        User findUser = userRepository.findByNickname(nickname);
        return findUser.getGrade() == Grade.MANAGER ? true : false;
    }

    public ReadUserForm readByNickname(String nickname) {
        User findUser = userRepository.findByNickname(nickname);
        if (findUser == null) {
            return null; // TODO: 2023-03-08 예외처리
        }
        ReadUserForm findReadUserForm = getReadUserForm(findUser);
        return findReadUserForm;
    }

    public UpdateUserForm getUpdateForm(String nickname) {
        User findUser = userRepository.findByNickname(nickname);
        if (findUser == null) {
            return null; // TODO: 2023-03-08 예외처리
        }
        UpdateUserForm updateForm = getUpdateUserForm(findUser);
        return updateForm;
    }

    @Transactional
    public Long updateUserInfo(UpdateUserForm updateForm) { // TODO: 2023-03-20 builder로 수정 필요
        User findUser = userRepository.findByNickname(updateForm.getNickname());
        findUser.setName(updateForm.getName());
        findUser.setAge(updateForm.getAge());
        findUser.setGender(updateForm.getGender());
        findUser.setDates(new Dates(findUser.getDates().getCreateDate(), LocalDateTime.now(), null));
        return findUser.getId();
    }

    @Transactional
    public Long changePassword(ChangePasswordForm changeForm, String nickname) {
        User findUser = userRepository.findByNickname(nickname);
        findUser.setPassword(changeForm.getAfterPassword());
        return findUser.getId();
    }

    @Transactional
    public void deleteUser(String nickname) {
        User findUser = userRepository.findByNickname(nickname);
        userRepository.delete(findUser); // TODO: 2023-03-20 복구 가능성을 위해 서비스 계층에서 아이디 보관
    }

    /**
     * ================== private method ====================
     */

    private static User createUser(SaveUserForm form) {
        User user = new User();
        user.setEmailId(form.getEmailId());
        user.setPassword(form.getPassword());
        user.setNickname(form.getNickname());
        user.setName(form.getName());
        user.setAge(form.getAge());
        user.setGender(form.getGender());
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        user.setGrade(Grade.BASIC);
        return user;
    }

    private static ReadUserForm getReadUserForm(User findUser) {
        ReadUserForm ReadUserForm = new ReadUserForm();
        ReadUserForm.setId(findUser.getId());
        ReadUserForm.setEmailId(findUser.getEmailId());
        ReadUserForm.setPassword(findUser.getPassword());
        ReadUserForm.setNickname(findUser.getNickname());
        ReadUserForm.setName(findUser.getName());
        ReadUserForm.setAge(findUser.getAge());
        ReadUserForm.setGender(findUser.getGender());
        ReadUserForm.setDates(findUser.getDates());
        return ReadUserForm;
    }

    private static UpdateUserForm getUpdateUserForm(User findUser) {
        UpdateUserForm userForm = new UpdateUserForm();
        userForm.setNickname(findUser.getNickname());
        userForm.setName(findUser.getName());
        userForm.setAge(findUser.getAge());
        userForm.setGender(findUser.getGender());
        return userForm;
    }
}
