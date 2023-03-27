package com.catdog.help.repository.comment;

import com.catdog.help.domain.board.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public Long save(Comment comment) {
        em.persist(comment);
        return comment.getId();
    }

    @Override
    public Comment findById(Long commentId) {
        return em.find(Comment.class, commentId);
    }

    @Override
    public List<Comment> findAll(Long boardId) {
        List<Comment> comments = em.createQuery("select distinct c from Comment c left join fetch c.child where c.parent = null and c.board.id = :boardId", Comment.class)
                .setParameter("boardId", boardId)
                .getResultList();

        return comments;
    }

    @Override
    public void delete(Comment comment) {
        em.remove(comment);
    }
}
