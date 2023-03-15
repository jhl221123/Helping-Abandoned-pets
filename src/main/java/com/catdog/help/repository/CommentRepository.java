package com.catdog.help.repository;

import com.catdog.help.domain.Board.Comment;

import java.util.List;

public interface CommentRepository {

    public Long save(Comment comment);

    public Comment read(Long boardId);

    public List<Comment> readAll(Long boardId);

    public void delete(Long id);
}
