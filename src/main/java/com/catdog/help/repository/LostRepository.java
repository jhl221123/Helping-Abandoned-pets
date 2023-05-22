package com.catdog.help.repository;

import com.catdog.help.domain.board.Lost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LostRepository extends JpaRepository<Lost, Long> {

    @Query("select count(l) from Lost l join l.user u where u.nickname = :nickname")
    Long countByNickname(@Param("nickname") String nickname);
}
