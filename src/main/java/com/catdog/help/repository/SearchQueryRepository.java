package com.catdog.help.repository;

import com.catdog.help.domain.board.*;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.catdog.help.MyConst.*;
import static com.catdog.help.domain.board.QBulletin.bulletin;
import static com.catdog.help.domain.board.QInquiry.inquiry;
import static com.catdog.help.domain.board.QItem.item;
import static com.catdog.help.domain.board.QLost.*;

@Repository
public class SearchQueryRepository {

    private final JPAQueryFactory queryFactory;

    public SearchQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    public Page<Bulletin> searchBulletin(String title, String region, Pageable pageable) {
        QueryResults<Bulletin> results = queryFactory
                .selectFrom(bulletin)
                .where(
                        titleContain(title, BULLETIN),
                        regionEq(region, BULLETIN)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bulletin.id.desc())
                .fetchResults();

        List<Bulletin> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<Lost> searchLost(String region, Pageable pageable) {
        QueryResults<Lost> results = queryFactory
                .selectFrom(lost)
                .where(
                        regionEq(region, LOST)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(lost.id.desc())
                .fetchResults();

        List<Lost> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<Item> searchItem(String region, String itemName, Pageable pageable) {
        QueryResults<Item> results = queryFactory
                .selectFrom(item)
                .where(
                        regionEq(region, ITEM),
                        itemNameContain(itemName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.id.desc())
                .fetchResults();

        List<Item> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<Inquiry> searchInquiry(String title, Pageable pageable) {
        QueryResults<Inquiry> results = queryFactory
                .selectFrom(inquiry)
                .where(
                        titleContain(title, INQUIRY)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(inquiry.id.desc())
                .fetchResults();

        List<Inquiry> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }


    private BooleanExpression itemNameContain(String itemName) {
        return itemName != null && !itemName.isBlank() ? item.itemName.contains(itemName) : null;
    }

    private BooleanExpression regionEq(String region, String boardType) {
        return region != null && !region.isBlank() ? returnEqBoard(region, boardType) : null;
    }

    private BooleanExpression titleContain(String title, String boardType) {
        return title != null && !title.isBlank() ? returnContainsBoard(title, boardType) : null;
    }

    private BooleanExpression returnContainsBoard(String title, String boardType) {
        if (boardType == BULLETIN) {
            return bulletin.title.contains(title);
        } else if (boardType == ITEM) {
            return item.title.contains(title);
        } else {
            return inquiry.title.contains(title);
        }
    }

    private BooleanExpression returnEqBoard(String region, String boardType) {
        if (boardType == BULLETIN) {
            return bulletin.region.eq(region);
        } else if (boardType == LOST) {
            return lost.region.eq(region);
        } else {
            return item.region.eq(region);
        }
    }
}
