package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {

    Page<Bulletin> findPageBy(Pageable pageable);
}
