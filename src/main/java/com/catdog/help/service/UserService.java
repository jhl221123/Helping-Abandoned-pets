package com.catdog.help.service;

import com.catdog.help.web.UserForm;
import com.catdog.help.domain.Gender;
import com.catdog.help.domain.User;
import com.catdog.help.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryImpl userRepositoryImpl;

    @Transactional
    public Long join(UserForm userForm) {
        User user = createUser(userForm.getEmailId(),
                userForm.getPassword(),
                userForm.getName(),
                userForm.getAge(),
                userForm.getGender());
        userRepositoryImpl.save(user);
        return user.getNo();
    }

    private static User createUser(String emailId, String password, String name, int age, String gender) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName(name);
        user.setAge(age);
        if (gender.equals("man")) {
            user.setGender(Gender.MAN);
        } else {
            user.setGender(Gender.WOMAN);
        }
        user.setJoinDate(LocalDateTime.now());
        user.setReliability(0);
        return user;
    }
}
