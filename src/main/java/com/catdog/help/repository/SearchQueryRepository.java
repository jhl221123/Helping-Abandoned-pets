package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.board.Item;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.catdog.help.domain.board.QBulletin.bulletin;
import static com.catdog.help.domain.board.QInquiry.inquiry;
import static com.catdog.help.domain.board.QItem.item;

@Repository
public class SearchQueryRepository {

    private static final String BULLETIN = "bulletin";
    private static final String ITEM = "item";
    private static final String INQUIRY = "inquiry";

    private final JPAQueryFactory queryFactory;

    public SearchQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    public Page<Bulletin> searchBulletin(String title, String region, Pageable pageable) {
        QueryResults<Bulletin> results = queryFactory
                .selectFrom(bulletin)
                .where(
                        titleContain(title, BULLETIN),
                        regionEq(region)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bulletin.id.desc())
                .fetchResults();

        List<Bulletin> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<Item> searchItem(String title, String itemName, Pageable pageable) {
        QueryResults<Item> results = queryFactory
                .selectFrom(item)
                .where(
                        titleContain(title, ITEM),
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

    private BooleanExpression regionEq(String region) {
        return region != null && !region.isBlank() ? bulletin.region.eq(region) : null;
    }

    private BooleanExpression titleContain(String title, String boardType) {
        return title != null && !title.isBlank() ? returnTargetBoard(title, boardType) : null;
    }

    private BooleanExpression returnTargetBoard(String title, String boardType) {
        if (boardType == BULLETIN) {
            return bulletin.title.contains(title);
        } else if (boardType == ITEM) {
            return item.title.contains(title);
        } else {
            return inquiry.title.contains(title);
        }
    }
}
