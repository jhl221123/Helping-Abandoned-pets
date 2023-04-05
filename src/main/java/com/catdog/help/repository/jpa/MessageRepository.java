package com.catdog.help.repository.jpa;

import com.catdog.help.domain.message.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final EntityManager em;

    public Long save(Message message) {
        em.persist(message);
        return message.getId();
    }

    public List<Message> findByRoomId(Long messageRoomId) {
        return em.createQuery("select m from Message m where message_room_id = :messageRoomId", Message.class)
                .setParameter("messageRoomId", messageRoomId)
                .getResultList();
    }
}
