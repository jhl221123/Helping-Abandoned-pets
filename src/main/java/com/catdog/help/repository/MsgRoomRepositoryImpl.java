package com.catdog.help.repository;

import com.catdog.help.domain.message.MsgRoom;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.catdog.help.domain.board.QItem.item;
import static com.catdog.help.domain.message.QMsgRoom.msgRoom;
import static com.catdog.help.domain.user.QUser.user;

public class MsgRoomRepositoryImpl implements MsgRoomRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public MsgRoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em;
    }

    @Override
    public Optional<MsgRoom> findWithRefers(Long id) {
        return Optional.ofNullable(queryFactory
                .selectFrom(msgRoom)
                .join(msgRoom.item, item).fetchJoin()
                .join(msgRoom.sender, user).fetchJoin()
                .join(msgRoom.recipient, user).fetchJoin()
                .where(msgRoom.id.eq(id))
                .fetchOne());
    }

    @Override
    public Optional<MsgRoom> findByRefers(Long userId, Long boardId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(msgRoom)
                .where(
                        senderIdEq(userId).or(recipientIdEq(userId))
                                .and(boardIdEq(boardId))
                )
                .fetchOne());
    }

    @Override
    public Page<MsgRoom> findPageByUserId(Long userId, Pageable pageable) {
        QueryResults<MsgRoom> results = queryFactory
                .selectFrom(msgRoom)
                .join(msgRoom.sender, user)
                .join(msgRoom.recipient, user)
                .where(
                        senderIdEq(userId).or(recipientIdEq(userId))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(msgRoom.id.desc())
                .fetchResults();

        List<MsgRoom> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Long countByUser(Long userId) {
        return queryFactory
                .selectFrom(msgRoom)
                .where(
                        senderIdEq(userId).or(recipientIdEq(userId))
                )
                .fetchCount();
    }


    private BooleanExpression senderIdEq(Long userId) {
        return userId != null ? msgRoom.sender.id.eq(userId) : null;
    }

    private BooleanExpression recipientIdEq(Long userId) {
        return userId != null ? msgRoom.recipient.id.eq(userId) : null;
    }

    private BooleanExpression boardIdEq(Long boardId) {
        return boardId != null ? msgRoom.item.id.eq(boardId) : null;
    }
}
