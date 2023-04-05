package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.repository.UploadFileRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class JpaUploadFileRepository implements UploadFileRepository {

    @PersistenceContext
    EntityManager em;

    public Long save(UploadFile uploadFile) {
        em.persist(uploadFile);
        return uploadFile.getId();
    }

    @Override
    public UploadFile findById(Long id) {
        return em.find(UploadFile.class, id);
    }

    public List<UploadFile> findUploadFiles(Long boardId) {
        return em.createQuery("select u from UploadFile u where u.board.id = :boardId", UploadFile.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    @Override
    public Long delete(UploadFile uploadFile) {
        em.remove(uploadFile);
        return uploadFile.getId();
    }
}