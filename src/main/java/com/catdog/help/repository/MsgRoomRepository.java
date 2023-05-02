package com.catdog.help.repository;

import com.catdog.help.domain.message.MsgRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgRoomRepository extends JpaRepository<MsgRoom, Long>, MsgRoomRepositoryCustom {

//    @Query("select mr from MsgRoom mr join fetch mr.item i" +
//                                        " join fetch mr.sender s" +
//                                        " join fetch mr.recipient r" +
//                                        " where mr.id = :id")
//    Optional<MsgRoom> findWithRefers(@Param("id") Long id);
//
//    @Query("select mr from MsgRoom mr where (sender_id = :userId or recipient_id = :userId) and mr.item.id = :boardId")
//    Optional<MsgRoom> findByRefers(@Param("userId") Long userId, @Param("boardId") Long boardId);
//
////    Page<MessageRoom> findPageBySenderIdOrRecipientId(@Param("userId") Long userId, Pageable pageable);
//// TODO: 2023-04-26 쿼리 DSL로 하자..ㅠ
//    @Query("select count(mr) from MsgRoom mr where sender_id = :userId or recipient_id = :userId")
//    Long countByUser(@Param("userId") Long userId);
}
