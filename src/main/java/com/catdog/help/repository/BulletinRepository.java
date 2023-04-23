package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {

    @Query("select b.user.nickname from Bulletin b where b.id = :id")// TODO: 2023-04-23 크로스 조인 발생. fetch join 이용해서 수정하자.
    String findNicknameById(@Param("id") Long id);

    Page<Bulletin> findPageBy(Pageable pageable);
}
