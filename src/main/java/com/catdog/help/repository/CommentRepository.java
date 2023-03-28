package com.catdog.help.repository.comment;

import com.catdog.help.domain.board.Comment;

import java.util.List;

public interface CommentRepository {

    public Long save(Comment comment);

    public Comment findById(Long commentId);

    public List<Comment> findAll(Long boardId);

    public void delete(Comment comment);
}
