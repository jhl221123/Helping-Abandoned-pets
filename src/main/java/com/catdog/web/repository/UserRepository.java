package com.catdog.web.repository;

import com.catdog.web.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepository {

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
