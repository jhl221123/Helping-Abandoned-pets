package com.catdog.help.service;

import com.catdog.help.web.UpdateBoardForm;
import com.catdog.help.domain.Board.BulletinBoard;
import com.catdog.help.web.SaveBoardForm;

import java.util.List;

public interface BulletinBoardService {

    public Long createBoard(SaveBoardForm boardForm);

    public UpdateBoardForm readBoard(Long boardNo);

    public Long updateBoard(UpdateBoardForm updateBoardForm);

    public List<UpdateBoardForm> readAll();

    public BulletinBoard readOne(Long boardNo);
}
