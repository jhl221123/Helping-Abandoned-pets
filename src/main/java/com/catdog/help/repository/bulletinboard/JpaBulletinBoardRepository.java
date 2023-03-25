package com.catdog.help.repository.bulletinboard;

import com.catdog.help.domain.board.BulletinBoard;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class JpaBulletinBoardRepository implements BulletinBoardRepository{

    @PersistenceContext
    EntityManager em;

    public Long save(BulletinBoard board) {
        em.persist(board);
        return board.getId();
    }

    public BulletinBoard findOne(Long id) {
        return em.find(BulletinBoard.class, id); //Optional 처리 어떻게 해야하는지 알아보자
    }

    public List<BulletinBoard> findAll() {
        List<BulletinBoard> boards = em.createQuery("select b from BulletinBoard b order by create_date desc", BulletinBoard.class).getResultList();
        return boards;
    }

    public Long delete(BulletinBoard board) {
        em.remove(board);
        return board.getId();
    }
}
