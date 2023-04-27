package com.catdog.help.repository;

import com.catdog.help.domain.message.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MsgRoomRepository extends JpaRepository<MessageRoom, Long> {

    @Query("select mr from MessageRoom mr join fetch mr.item i" +
                                        " join fetch mr.sender s" +
                                        " join fetch mr.recipient r" +
                                        " where mr.id = :id")
    Optional<MessageRoom> findWithRefers(@Param("id") Long id);

    @Query("select mr from MessageRoom mr where sender_id = :userId or recipient_id = :userId")
    List<MessageRoom> findAllByUser(@Param("userId") Long userId);

//    Page<MessageRoom> findPageBySenderIdOrRecipientId(@Param("userId") Long userId, Pageable pageable);
// TODO: 2023-04-26 쿼리 DSL로 하자..ㅠ
    @Query("select count(mr) from MessageRoom mr where sender_id = :userId or recipient_id = :userId")
    Long countByUser(@Param("userId") Long userId);
}
