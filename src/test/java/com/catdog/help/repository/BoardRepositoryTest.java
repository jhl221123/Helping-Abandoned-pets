package com.catdog.help.repository;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;


    @Test
    @DisplayName("Board 타입 단건 조회")
    void findBoard() {
        //given
        BulletinBoard bulletin = getBulletin();
        boardRepository.save(bulletin);

        //when
        Board findBoard = boardRepository.findById(bulletin.getId()).get();

        //then
        Assertions.assertThat(findBoard).isEqualTo(bulletin);
    }

    private BulletinBoard getBulletin() {
        return BulletinBoard.builder()
                .user(User.builder().build())
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }
}