package com.catdog.web.service;

import com.catdog.web.controller.UserForm;
import com.catdog.web.domain.Gender;
import com.catdog.web.domain.User;
import com.catdog.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long join(UserForm userForm) {
        User user = createUser(userForm.getEmailId(),
                userForm.getPassword(),
                userForm.getName(),
                userForm.getAge(),
                userForm.getGender());
        userRepository.save(user);
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
