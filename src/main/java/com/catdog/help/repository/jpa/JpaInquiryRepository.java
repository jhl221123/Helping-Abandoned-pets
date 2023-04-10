package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.Inquiry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaInquiryRepository {

    private final EntityManager em;

    public Long save(Inquiry inquiry) {
        em.persist(inquiry);
        return inquiry.getId();
    }

    public Inquiry findById(Long id) {
        return em.find(Inquiry.class, id);
    }

    public List<Inquiry> findPage(int offset, int limit) {
        return em.createQuery("select i from Inquiry i order by i.dates.createDate desc", Inquiry.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public void delete(Inquiry inquiry) {
        em.remove(inquiry);
    }
}
