package com.catdog.help.repository;

import com.catdog.help.domain.board.Lost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostRepository extends JpaRepository<Lost, Long> {
}
