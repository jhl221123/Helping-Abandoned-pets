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

    public MessageRoom findById(Long id) {
        return em.find(MessageRoom.class, id);
    }

    public MessageRoom findWithRefers(Long id) {
        return em.createQuery("select mr from MessageRoom mr" +
                        " join fetch mr.itemBoard i" +
                        " join fetch mr.sender s" +
                        " join fetch mr.recipient r" +
                        " join fetch mr.messages ms" +
                        " where mr.id = :id", MessageRoom.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findAny()
                .orElse(null);
    }

    public List<MessageRoom> findAllByUserId(Long userId) {
        return em.createQuery("select mr from MessageRoom mr where sender_id = :userId or recipient_id = :userId", MessageRoom.class)
                .setParameter("userId", userId)
                .getResultList();
        // TODO: 2023-04-06 생성일 추가 후  order by create_date desc
    }
}
