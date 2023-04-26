package com.catdog.help.repository;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    BulletinRepository bulletinRepository;

    @Autowired
    UserRepository userRepository;


    @Test
    @DisplayName("Board 타입 단건 조회")
    void findBoard() {
        //given
        Bulletin bulletin = getBulletin();
        boardRepository.save(bulletin);

        //when
        Board findBoard = boardRepository.findById(bulletin.getId()).get();

        //then
        Assertions.assertThat(findBoard).isEqualTo(bulletin);
    }

    @Test
    @DisplayName("id로 닉네임 조회")
    void findNicknameById() {
        //given
        Bulletin board = getBulletin();
        Bulletin savedBoard = bulletinRepository.save(board);

        //when
        String nickname = boardRepository.findNicknameById(savedBoard.getId());

        //then
        assertThat(nickname).isEqualTo(board.getUser().getNickname());
    }

    private Bulletin getBulletin() {
        User user = User.builder().build();
        userRepository.save(user);

        return Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }
}