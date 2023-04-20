package com.catdog.help.service;

import com.catdog.help.MyConst;
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

import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository userRepository;

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
        return userRepository.save(user);
    }

    public boolean isEmailDuplication(String email) {
        Optional<User> findUser = userRepository.findByEmailId(email);
        return findUser.isPresent() ? true : false;
    }

    public boolean isNicknameDuplication(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        return findUser.isPresent() ? true : false;
    }

    @Transactional
    public String login(String emailId, String password) {
        Optional<User> findUser = userRepository.findByEmailId(emailId); // TODO: 2023-04-20 orElseGet(null) 사용 시 nullPointer 발생. get() 안 쓰도록 고민해보자.
        return findUser.isEmpty() ? MyConst.FAIL_LOGIN :
               findUser.get().getPassword().equals(password) ? findUser.get().getNickname() : MyConst.FAIL_LOGIN;
    }

    public Boolean isManager(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        return findUser.get().getGrade() == Grade.MANAGER ? true : false;
    }

    public ReadUserForm readByNickname(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        if (findUser == null) {
            return null; // TODO: 2023-03-08 예외처리
        }
        return new ReadUserForm(findUser.get());
    }

    public UpdateUserForm getUpdateForm(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        if (findUser == null) {
            return null; // TODO: 2023-03-08 예외처리
        }
        return new UpdateUserForm(findUser.get());
    }

    @Transactional
    public Long updateUserInfo(UpdateUserForm form) {
        Optional<User> findUser = userRepository.findByNickname(form.getNickname());
        findUser.get().updateUser(form.getName(), form.getAge(), form.getGender());
        return findUser.get().getId();
    }

    @Transactional
    public Long changePassword(ChangePasswordForm form, String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        findUser.get().changePassword(form.getAfterPassword());
        return findUser.get().getId();
    }

    @Transactional
    public void deleteUser(String nickname) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        userRepository.delete(findUser.get()); // TODO: 2023-03-20 복구 가능성을 위해 서비스 계층에서 아이디 보관
    }
}
