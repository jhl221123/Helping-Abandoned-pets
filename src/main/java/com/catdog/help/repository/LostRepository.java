package com.catdog.help.repository;

import com.catdog.help.domain.board.Lost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LostRepository extends JpaRepository<Lost, Long> {

    @Query("select count(l) from Lost l join l.user u where u.nickname = :nickname")
    Long countByNickname(@Param("nickname") String nickname);

    @Query("select l from Lost l join l.user u where u.nickname = :nickname")
    Page<Lost> findPageByNickname(@Param("nickname") String nickname, Pageable pageable);
}
