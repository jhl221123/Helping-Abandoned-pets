package com.catdog.help.repository;

import com.catdog.help.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
