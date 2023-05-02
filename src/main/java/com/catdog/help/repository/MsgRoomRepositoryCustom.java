package com.catdog.help.repository;

import com.catdog.help.domain.message.MsgRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MsgRoomRepositoryCustom {

    Optional<MsgRoom> findWithRefers(Long id);

    Optional<MsgRoom> findByRefers(Long userId, Long boardId);

    Page<MsgRoom> findPageByUserId(Long userId, Pageable pageable);

    Long countByUser(Long userId);
}
