package com.catdog.help.repository.jpa;

import com.catdog.help.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Slf4j
@Repository
public class JpaUserRepository {

    @PersistenceContext
    EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public User findByEmailId(String emailId) {
        List<User> findUser = em.createQuery("select u from User u where u.emailId = :emailId", User.class)
                .setParameter("emailId", emailId)
                .getResultList();
        return findUser.stream().findAny().orElse(null);
    }

    public User findByNickname(String nickname) {
        List<User> findUser = em.createQuery("select u from User u where u.nickname = :nickname", User.class)
                .setParameter("nickname", nickname)
                .getResultList();
        return findUser.stream().findAny().orElse(null);
    }

    public List<User> findAll() {
        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        return users;
    }

    public Long delete(User user) {
        em.remove(user);
        return user.getId();
    }
}
