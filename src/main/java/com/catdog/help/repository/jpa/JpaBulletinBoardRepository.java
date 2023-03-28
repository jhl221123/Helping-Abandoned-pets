package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.repository.BulletinBoardRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class JpaBulletinBoardRepository implements BulletinBoardRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(BulletinBoard board) {
        em.persist(board);
        return board.getId();
    }

    public BulletinBoard findOne(Long id) {
        return em.find(BulletinBoard.class, id); //Optional 처리 어떻게 해야하는지 알아보자
    }

    public List<BulletinBoard> findPage(int start, int end) {
        return em.createQuery("select b from BulletinBoard b order by create_date desc", BulletinBoard.class)
                .setFirstResult(start)
                .setMaxResults(end)
                .getResultList();
    }

    public List<BulletinBoard> findAll() {
        return em.createQuery("select b from BulletinBoard b order by create_date desc", BulletinBoard.class).getResultList();
    }

    public Long delete(BulletinBoard board) {
        em.remove(board);
        return board.getId();
    }
}
