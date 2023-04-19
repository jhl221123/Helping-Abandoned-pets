package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.Board;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class JpaBoardRepository {

    @PersistenceContext
    EntityManager em;

    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(em.find(Board.class, id));
    }

}
