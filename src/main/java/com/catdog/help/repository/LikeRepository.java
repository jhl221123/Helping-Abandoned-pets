package com.catdog.help.repository;

import com.catdog.help.domain.board.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select l from Like l where l.board.id = :boardId and l.user.id = :userId")
    Optional<Like> findByIds(@Param("boardId") Long boardId, @Param("userId") Long userId);

    @Query("select count(l) from Like l where l.board.id = :boardId")
    Long countByBoardId(@Param("boardId") Long boardId);
}
