package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
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
                        titleContain(title),
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


    private BooleanExpression regionEq(String region) {
        return region != null && !region.isBlank() ? bulletin.region.eq(region) : null;
    }

    private BooleanExpression titleContain(String title) {
        return title != null && !title.isBlank() ? bulletin.title.contains(title) : null;
    }
}
