package com.catdog.help.repository;

import com.catdog.help.domain.board.LikeBoard;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class JpaLikeBoardRepository implements LikeBoardRepository{

    @PersistenceContext
    EntityManager em;

    public Long save(LikeBoard likeBoard) {
        em.persist(likeBoard);
        return likeBoard.getId();
    }

    public LikeBoard findById(Long likeBoardId) {
        return null; // TODO: 2023-03-26 spring data jpa 할 때 손보기
    }

    public LikeBoard findByIds(Long boardId, Long userId) {
        List<LikeBoard> result = em.createQuery("select l from LikeBoard l where l.board.id = :boardId and l.user.id = :userId", LikeBoard.class)
                .setParameter("boardId", boardId)
                .setParameter("userId", userId)
                .getResultList();
        return result.stream().findAny().orElse(null);
    }

    public List<LikeBoard> findAllByBoardId(Long boardId) {
        List<LikeBoard> result = em.createQuery("select l from LikeBoard l where l.board.id = :boardId", LikeBoard.class)
                .setParameter("boardId", boardId)
                .getResultList();
        return result;
    }

    public void delete(LikeBoard likeBoard) {
        em.remove(likeBoard);
    }
}
