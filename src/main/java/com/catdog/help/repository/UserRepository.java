package com.catdog.help.repository;

import com.catdog.help.domain.User;

import java.util.List;

public interface UserRepository {
    public void save(User user);

    public User findOne(Long no);

    public List<User> findAll();
}
