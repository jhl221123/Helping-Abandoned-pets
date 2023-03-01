package com.catdog.web.repository;

import com.catdog.web.domain.Board.Board;

import java.util.List;

public interface BoardRepository {

    public void save();

    public Board findOne();

    public List<Board> findAll();
}
