package com.catdog.help.service;

import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BoardServiceTest {

    @Autowired BoardService boardService;
    @Autowired JpaBulletinBoardRepository bulletinBoardRepository;


    @Test
    @DisplayName("게시글인지 확인")
    void isBulletin() {
        //given
        BulletinBoard bulletin = BulletinBoard.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .build();
        bulletinBoardRepository.save(bulletin);

        //when
        Boolean result = boardService.isBulletin(bulletin.getId());

        //then
        Assertions.assertThat(result).isTrue();
    }
}