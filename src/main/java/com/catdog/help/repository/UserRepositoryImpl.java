package com.catdog.help.repository;

import com.catdog.help.domain.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findOne(Long no) {
        return em.find(User.class, no);
    }

    public List<User> findAll() {
        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        return users;
    }
}
