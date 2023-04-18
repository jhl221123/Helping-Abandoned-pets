package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.BulletinBoard;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class JpaBulletinBoardRepository{

    @PersistenceContext
    EntityManager em;

    public Long save(BulletinBoard board) {
        em.persist(board);
        return board.getId();
    }

    public BulletinBoard findById(Long id) {
        return em.createQuery("select b from BulletinBoard b where b.id = :id", BulletinBoard.class)
                .setParameter("id", id)
                .getResultList().stream().findAny().orElse(null);
    }

    public List<BulletinBoard> findPage(int offset, int limit) {
        return em.createQuery("select b from BulletinBoard b order by b.createdDate desc", BulletinBoard.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("select count(b) from BulletinBoard b", Long.class).getSingleResult();
    }

    public List<BulletinBoard> findAll() { // TODO: 2023-03-28 카운트 쿼리로 대체 고려
        return em.createQuery("select b from BulletinBoard b order by b.createdDate desc", BulletinBoard.class).getResultList();
    }

    public Long delete(BulletinBoard board) {
        em.remove(board);
        return board.getId();
    }
}
