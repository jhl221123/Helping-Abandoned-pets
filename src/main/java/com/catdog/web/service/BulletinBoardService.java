package com.catdog.web.service;

import com.catdog.web.controller.BoardForm;
import com.catdog.web.domain.Board.BulletinBoard;

import java.util.List;

public interface BulletinBoardService {

    public Long createBoard(BoardForm boardForm);

    public BoardForm readBoard(Long boardNo);

    public Long updateBoard(BoardForm boardForm);

    public List<BoardForm> readAll();

    public BulletinBoard readOne(Long boardNo);
}
