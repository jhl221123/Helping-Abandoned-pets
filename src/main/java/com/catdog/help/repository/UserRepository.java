package com.catdog.help.repository;

import com.catdog.help.domain.user.User;

import java.util.List;

public interface UserRepository {
    public void save(User user);

    public User findById(Long id);

    public User findByEmailId(String emailId);

    public User findByNickName(String nickName);

    public List<User> findAll();

    public Long delete(User user);
}
