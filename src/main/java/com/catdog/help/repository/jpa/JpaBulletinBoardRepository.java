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

    public BulletinBoard findById(Long id) {
        //게시글은 작성자 이름이 같이 따라다녀서 fetch join을 사용
        return em.createQuery("select b from BulletinBoard b join fetch b.user where board_id = :id", BulletinBoard.class)
                .setParameter("id", id)
                .getResultList().stream().findAny().orElse(null); //TODO: 2023-03-30 Optional
    }

    public List<BulletinBoard> findPage(int start, int total) {
        return em.createQuery("select b from BulletinBoard b order by create_date desc", BulletinBoard.class)
                .setFirstResult(start)
                .setMaxResults(total)
                .getResultList();
    }

    public List<BulletinBoard> findAll() { // TODO: 2023-03-28 카운트 쿼리로 대체 고려
        return em.createQuery("select b from BulletinBoard b order by create_date desc", BulletinBoard.class).getResultList();
    }

    public Long delete(BulletinBoard board) {
        em.remove(board);
        return board.getId();
    }
}
