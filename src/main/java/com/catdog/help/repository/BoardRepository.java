package com.catdog.help.repository;

import com.catdog.help.domain.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardQueryRepository {

    @Query("select u.nickname from Board b join b.user u where b.id = :id")
    String findNicknameById(@Param("id") Long id);
}