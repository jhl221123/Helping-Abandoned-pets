package com.catdog.help.service;

import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.SaveUserForm;
import com.catdog.help.domain.Gender;
import com.catdog.help.domain.User;
import com.catdog.help.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long join(SaveUserForm saveUserForm) {
        User user = createUser(saveUserForm.getEmailId(),
                saveUserForm.getPassword(),
                saveUserForm.getNickName(),
                saveUserForm.getName(),
                saveUserForm.getAge(),
                saveUserForm.getGender());
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public UserDto login(String emailId, String password) {
        User findUser = userRepository.findByEmailId(emailId);
        if (findUser == null || !findUser.getPassword().equals(password)) {
            return null;
        }

        UserDto findUserDto = getUserDto(findUser);

        return findUserDto;
    }

    private static UserDto getUserDto(User findUser) {
        UserDto UserDto = new UserDto();
        UserDto.setId(findUser.getId());
        UserDto.setEmailId(findUser.getEmailId());
        UserDto.setPassword(findUser.getPassword());
        UserDto.setNickName(findUser.getNickName());
        UserDto.setName(findUser.getName());
        UserDto.setAge(findUser.getAge());
        UserDto.setGender(findUser.getGender());
        UserDto.setReliability(findUser.getReliability());
        UserDto.setJoinDate(findUser.getJoinDate());
        return UserDto;
    }

    private static User createUser(String emailId, String password, String nickName, String name, int age, String gender) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setNickName(nickName);
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
