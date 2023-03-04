package com.catdog.help.repository;

import com.catdog.help.domain.Board.BulletinBoard;

import java.util.List;

public interface BulletinBoardRepository {

    public void save(BulletinBoard bulletinBoard);

    public BulletinBoard findOne(Long id);

    public List<BulletinBoard> findAll();
}
