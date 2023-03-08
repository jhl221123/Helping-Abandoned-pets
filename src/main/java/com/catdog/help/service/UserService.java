package com.catdog.help.service;

import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.SaveUserForm;
import com.catdog.help.domain.Gender;
import com.catdog.help.domain.User;
import com.catdog.help.repository.UserRepositoryImpl;
import com.catdog.help.web.form.UpdateUserForm;
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
        User user = createUser(saveUserForm.getEmailId(), // TODO: 2023-03-08 ?? ㅋㅋ 수정필요
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

    public UserDto getUserDtoByNickName(String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        if (findUser == null) {
            // TODO: 2023-03-06 예외처리
        }
        UserDto findUserDto = getUserDto(findUser);
        return findUserDto;
    }

    public boolean checkEmailDuplication(String email) {
        User findUser = userRepository.findByEmailId(email);
        if (findUser == null) {
            return false;
        }

        return true;
    }

    public boolean checkNickNameDuplication(String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        if (findUser == null) {
            return false;
        }

        return true;
    }

    public UpdateUserForm getUpdateForm(String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        if (findUser == null) {
            // TODO: 2023-03-08 예외처리
        }
        UpdateUserForm updateForm = getUpdateUserForm(findUser);
        return updateForm;
    }

    @Transactional
    public Long updateUserInfo(UpdateUserForm updateForm) {
        User findUser = userRepository.findByNickName(updateForm.getNickName());
        findUser.setName(updateForm.getName());
        findUser.setAge(updateForm.getAge());
        if (updateForm.getGender().equals("남자")) {
            findUser.setGender(Gender.MAN);
        } else {
            findUser.setGender(Gender.WOMAN);
        }
        return findUser.getId();
    }

    /**
     * detach용으로 만들었는데 좀 더 공부해서 준영속이랑 같은 거면 detach로 로직을 변경하자.
     */
    public User getUser(UserDto findUserDto) {
        User user = new User();
        user.setId(findUserDto.getId());
        user.setEmailId(findUserDto.getEmailId());
        user.setPassword(findUserDto.getPassword());
        user.setNickName(findUserDto.getNickName());
        user.setName(findUserDto.getName());
        user.setAge(findUserDto.getAge());
        user.setReliability(findUserDto.getReliability());
        user.setJoinDate(findUserDto.getJoinDate());
        return user;
    }

    private static UserDto getUserDto(User findUser) {
        UserDto UserDto = new UserDto();
        UserDto.setId(findUser.getId());
        UserDto.setEmailId(findUser.getEmailId());
        UserDto.setPassword(findUser.getPassword());
        UserDto.setNickName(findUser.getNickName());
        UserDto.setName(findUser.getName());
        UserDto.setAge(findUser.getAge());
        if (findUser.getGender() == Gender.MAN) {
            UserDto.setGender("남자");
        } else {
            UserDto.setGender("여자");
        }
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

    private static UpdateUserForm getUpdateUserForm(User findUser) {
        UpdateUserForm userForm = new UpdateUserForm();
        userForm.setNickName(findUser.getNickName());
        userForm.setName(findUser.getName());
        userForm.setAge(findUser.getAge());
        if (findUser.getGender() == Gender.MAN) {
            userForm.setGender("남자");
        } else {
            userForm.setGender("여자");
        }
        return userForm;
    }
}
