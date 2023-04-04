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
        return em.createQuery("select i from ItemBoard i join fetch i.user where board_id = :id", ItemBoard.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findAny()
                .orElse(null);
    }

    public List<ItemBoard> findPage(int start, int total) {
        return em.createQuery("select i from ItemBoard i order by create_date desc", ItemBoard.class)
                .setFirstResult(start)
                .setMaxResults(total)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("select count(i) from ItemBoard i", Long.class)
                .getResultList()
                .stream()
                .findAny()
                .orElse(0L);
    }

    public List<ItemBoard> findAll() {
        return em.createQuery("select i from ItemBoard i order by create_date desc", ItemBoard.class).getResultList();
    }

    public Long delete(ItemBoard board) {
        em.remove(board);
        return board.getId();
    }
}
