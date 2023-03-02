package com.catdog.web.repository;

import com.catdog.web.domain.User;

import java.util.List;

public interface UserRepository {
    public void save(User user);

    public User findOne(Long no);

    public List<User> findAll();
}
