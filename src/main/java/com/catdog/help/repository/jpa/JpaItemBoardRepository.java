package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.ItemBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaItemBoardRepository {

    private final EntityManager em;

    public Long save(ItemBoard board) {
        em.persist(board);
        return board.getId();
    }

    public ItemBoard findById(Long id) {
        return em.find(ItemBoard.class, id);
    }

    public List<ItemBoard> findPage(int start, int end) {
        return em.createQuery("select i from ItemBoard i order by create_date desc", ItemBoard.class)
                .setFirstResult(start)
                .setMaxResults(end)
                .getResultList();
    }

    public List<ItemBoard> findAll() { // TODO: 2023-03-28 카운트 쿼리로 대체 고려
        return em.createQuery("select i from ItemBoard i order by create_date desc", ItemBoard.class).getResultList();
    }

    public Long delete(ItemBoard board) {
        em.remove(board);
        return board.getId();
    }
}
