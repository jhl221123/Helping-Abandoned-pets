package com.catdog.help.service;

import com.catdog.help.MyConst;
import com.catdog.help.domain.user.Grade;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.NotFoundUserException;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.user.ChangePasswordForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long join(SaveUserForm form) {
        User user = User.builder()
                .emailId(form.getEmailId())
                .password(form.getPassword())
                .nickname(form.getNickname())
                .name(form.getName())
                .age(form.getAge())
                .gender(form.getGender())
                .build();
        return userRepository.save(user).getId();
    }

    public boolean isEmailDuplication(String email) {
        Optional<User> findUser = userRepository.findByEmailId(email);
        return findUser.isPresent() ? true : false;
    }

    public boolean isNicknameDuplication(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        return findUser.isPresent() ? true : false;
    }

    public String login(String emailId, String password) {
        Optional<User> findUser = userRepository.findByEmailId(emailId);
        return findUser.isEmpty() ? MyConst.FAIL_LOGIN :
               findUser.get().getPassword().equals(password) ? findUser.get().getNickname() : MyConst.FAIL_LOGIN;
    }

    public Boolean isManager(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(NotFoundUserException::new);
        return findUser.getGrade() == Grade.MANAGER ? true : false;
    }

    public ReadUserForm readByNickname(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(NotFoundUserException::new);
        return new ReadUserForm(findUser);
    }

    public UpdateUserForm getUpdateForm(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(NotFoundUserException::new);
        return new UpdateUserForm(findUser);
    }

    @Transactional
    public Long updateUserInfo(UpdateUserForm form) {
        User findUser = userRepository.findByNickname(form.getNickname())
                .orElseThrow(NotFoundUserException::new);
        findUser.updateUser(form.getName(), form.getAge(), form.getGender());
        return findUser.getId();
    }

    public Boolean isSamePassword(String password, String nickname) {
        String target = userRepository.findByNickname(nickname)
                .orElseThrow(NotFoundUserException::new)
                .getPassword();
        return password.equals(target) ? true : false;
    }

    @Transactional
    public Long changePassword(ChangePasswordForm form, String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(NotFoundUserException::new);
        findUser.changePassword(form.getAfterPassword());
        return findUser.getId();
    }

    @Transactional
    public void deleteUser(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(NotFoundUserException::new);
        userRepository.delete(findUser);
    }
}
