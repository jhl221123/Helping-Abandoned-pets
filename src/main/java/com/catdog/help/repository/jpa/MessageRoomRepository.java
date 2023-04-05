package com.catdog.help.repository.jpa;

import com.catdog.help.domain.message.MessageRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRoomRepository {

    private final EntityManager em;

    public Long save(MessageRoom messageRoom) {
        em.persist(messageRoom);
        return messageRoom.getId();
    }

    public List<MessageRoom> findAllByUserId(Long userId) {
        return em.createQuery("select mr from MessageRoom mr where sender_id = :userId or recipient_id = :userId", MessageRoom.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
