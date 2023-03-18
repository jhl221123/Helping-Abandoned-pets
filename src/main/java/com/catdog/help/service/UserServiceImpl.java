package com.catdog.help.service;

import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.user.UpdateUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long join(SaveUserForm form) {
        User user = createUser(form.getEmailId(), // TODO: 2023-03-08 ?? ㅋㅋ 수정필요, 근데 setter 없으면 이게 맞는데..
                form.getPassword(),
                form.getNickName(),
                form.getName(),
                form.getAge(),
                form.getGender());
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

    public boolean checkNickNameDuplication(String nickName) {// TODO: 2023-03-08 private으로 수정 후 join에서 처리하도록 수정
        User findUser = userRepository.findByNickName(nickName);
        if (findUser == null) {
            return false;
        }
        return true;
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
            return null; // TODO: 2023-03-08 예외처리
        }
        UserDto findUserDto = getUserDto(findUser);
        return findUserDto;
    }

    public UpdateUserForm getUpdateForm(String nickName) {
        User findUser = userRepository.findByNickName(nickName);
        if (findUser == null) {
            return null; // TODO: 2023-03-08 예외처리
        }
        UpdateUserForm updateForm = getUpdateUserForm(findUser);
        return updateForm;
    }

    @Transactional
    public Long updateUserInfo(UpdateUserForm updateForm) {
        User findUser = userRepository.findByNickName(updateForm.getNickName());
        findUser.setName(updateForm.getName());
        findUser.setAge(updateForm.getAge());
        findUser.setGender(updateForm.getGender());
        return findUser.getId();
    }

    /**
     * ================== private method ====================
     */
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

    private static User createUser(String emailId, String password, String nickName, String name, int age, Gender gender) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setNickName(nickName);
        user.setName(name);
        user.setAge(age);
        user.setGender(gender);
        user.setJoinDate(LocalDateTime.now());
        user.setReliability(0);
        return user;
    }

    private static UpdateUserForm getUpdateUserForm(User findUser) {
        UpdateUserForm userForm = new UpdateUserForm();
        userForm.setNickName(findUser.getNickName());
        userForm.setName(findUser.getName());
        userForm.setAge(findUser.getAge());
        userForm.setGender(findUser.getGender());
        return userForm;
    }
}
