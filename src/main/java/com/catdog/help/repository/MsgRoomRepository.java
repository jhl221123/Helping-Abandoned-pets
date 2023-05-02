package com.catdog.help.repository;

import com.catdog.help.domain.message.MsgRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgRoomRepository extends JpaRepository<MsgRoom, Long>, MsgRoomRepositoryCustom {
}
