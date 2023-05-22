package com.catdog.help.repository;

import com.catdog.help.domain.board.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("select count(i) from Inquiry i join i.user u where u.nickname = :nickname")
    Long countByNickname(@Param("nickname") String nickname);

    @Query("select i from Inquiry i join i.user where i.user.nickname = :nickname")
    Page<Inquiry> findPageByNickname(@Param("nickname") String nickname, Pageable pageable);

    Page<Inquiry> findPageBy(Pageable pageable);
}
