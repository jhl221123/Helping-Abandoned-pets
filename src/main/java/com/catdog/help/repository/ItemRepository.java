package com.catdog.help.repository;

import com.catdog.help.domain.board.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i join i.user where i.user.nickname = :nickname")
    Page<Item> findPageByNickname(@Param("nickname") String nickname, Pageable pageable);

    @Query(value = "select * from BOARD b" +
            " join LIKES l on l.board_id = b.board_id" +
            " and l.user_id = :id" +
            " where b.dtype = 'Item'", nativeQuery = true)
    Page<Item> findLikeItems(@Param("id") Long id, Pageable pageable);

    Page<Item> findPageBy(Pageable pageable);
}
