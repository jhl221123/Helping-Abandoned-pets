package com.catdog.web.repository;

import com.catdog.web.domain.Board.BulletinBoard;

import java.util.List;

public interface BulletinBoardRepository {

    public void save(BulletinBoard bulletinBoard);

    public BulletinBoard findOne(Long id);

    public List<BulletinBoard> findAll();
}
