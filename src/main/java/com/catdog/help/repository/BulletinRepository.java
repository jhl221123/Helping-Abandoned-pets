package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {

    @Query("select b from Bulletin b join b.user where b.user.nickname = :nickname")
    Page<Bulletin> findPageByNickname(@Param("nickname") String nickname, Pageable pageable);

    @Query(value = "select * from board b" +
            " join likes l on l.board_id = b.board_id" +
            " and l.user_id = :id" +
            " where b.dtype = 'Bulletin'", nativeQuery = true)
    Page<Bulletin> findLikeBulletins(@Param("id") Long id, Pageable pageable);

    Page<Bulletin> findPageBy(Pageable pageable);
}
