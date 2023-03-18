package com.catdog.help.repository;

import com.catdog.help.domain.board.BulletinBoard;

import java.util.List;

public interface BulletinBoardRepository {

    public Long save(BulletinBoard bulletinBoard);

    public BulletinBoard findOne(Long id);

    public List<BulletinBoard> findAll();

    public void delete(BulletinBoard board);
}
