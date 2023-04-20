package com.catdog.help.repository;

import com.catdog.help.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailId(String emailId);

    Optional<User> findByNickname(String nickname);
}
