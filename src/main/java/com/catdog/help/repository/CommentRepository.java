package com.catdog.help.repository;

import com.catdog.help.domain.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c.user.nickname from Comment c join c.user u where c.id = :id")
    String findNicknameById(@Param("id") Long id);

    @Query("select distinct c from Comment c" +
            " join fetch c.board b" +
            " join fetch c.user u" +
            " left join fetch c.child" +
            " where c.parent = null and c.board.id = :boardId")
    List<Comment> findByBoardId(@Param("boardId") Long boardId);
}
