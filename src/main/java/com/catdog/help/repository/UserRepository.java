package com.catdog.help.repository.user;

import com.catdog.help.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    public void save(User user);

    public User findById(Long id);

    public User findByEmailId(String emailId);

    public User findByNickName(String nickName);

    public List<User> findAll();

    public Long delete(User user);
}
