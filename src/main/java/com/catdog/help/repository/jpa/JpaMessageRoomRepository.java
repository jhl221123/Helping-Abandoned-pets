package com.catdog.help.repository.jpa;

import com.catdog.help.domain.message.MessageRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaMessageRoomRepository {

    private final EntityManager em;

    public Long save(MessageRoom messageRoom) {
        em.persist(messageRoom);
        return messageRoom.getId();
    }

    public MessageRoom findById(Long id) {
        return em.find(MessageRoom.class, id);
    }

    public MessageRoom findWithRefers(Long id) {
        MessageRoom messageRoom = em.createQuery("select mr from MessageRoom mr" +
                        " join fetch mr.itemBoard i" +
                        " join fetch mr.sender s" +
                        " join fetch mr.recipient r" +
                        " where mr.id = :id", MessageRoom.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findAny()
                .orElse(null);
        return messageRoom;
    }

    public List<MessageRoom> findAllByUserId(Long userId) {
        return em.createQuery("select mr from MessageRoom mr where sender_id = :userId or recipient_id = :userId", MessageRoom.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<MessageRoom> findPageByUserId(Long userId, int offset, int limit) {
        return em.createQuery("select mr from MessageRoom mr where sender_id = :userId or recipient_id = :userId order by create_date desc", MessageRoom.class)
                .setParameter("userId", userId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long countAllByUserId(Long userId) {
        return em.createQuery("select count(mr) from MessageRoom mr where sender_id = :userId or recipient_id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

}
