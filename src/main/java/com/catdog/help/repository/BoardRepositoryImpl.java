package com.catdog.help.repository;

import com.catdog.help.web.form.BoardByRegion;
import com.catdog.help.web.form.QBoardByRegion;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.catdog.help.domain.board.QBulletin.bulletin;
import static com.catdog.help.domain.board.QItem.item;
import static com.catdog.help.domain.board.QLost.lost;

public class BoardRepositoryImpl implements BoardQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    public BoardRepositoryImpl(EntityManager em) {
        this.em = em;
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<BoardByRegion> countLostByRegion() {
        return jpaQueryFactory
                .select(
                        new QBoardByRegion(lost.region, lost.id.count().as("count"))
                )
                .from(lost)
                .groupBy(lost.region)
                .fetch();
    }

    @Override
    public List<BoardByRegion> countBulletinByRegion() {
        return jpaQueryFactory
                .select(
                        new QBoardByRegion(bulletin.region, bulletin.id.count().as("count"))
                )
                .from(bulletin)
                .groupBy(bulletin.region)
                .fetch();
    }

    @Override
    public List<BoardByRegion> countItemByRegion() {
        return jpaQueryFactory
                .select(
                        new QBoardByRegion(item.region, item.id.count().as("count"))
                )
                .from(item)
                .groupBy(item.region)
                .fetch();
    }
}
