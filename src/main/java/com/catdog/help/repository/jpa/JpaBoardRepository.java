package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.Board;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class JpaBoardRepository {

    @PersistenceContext
    EntityManager em;

    public Board findById(Long id) {
        return em.find(Board.class, id);
    }

}
