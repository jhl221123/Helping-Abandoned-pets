package com.catdog.help.repository;

import com.catdog.help.web.form.BoardByRegion;

import java.util.List;

public interface BoardQueryRepository {

    List<BoardByRegion> countLostByRegion();
    List<BoardByRegion> countBulletinByRegion();
    List<BoardByRegion> countItemByRegion();
}
