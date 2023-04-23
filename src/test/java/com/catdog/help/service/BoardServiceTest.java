package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.BoardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@Transactional
class BoardServiceTest {

    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;


    @Test
    @DisplayName("게시글인지 확인")
    void isBulletin() {
        //given
        Bulletin bulletin = getBulletin();

        doReturn(Optional.ofNullable(bulletin)).when(boardRepository)
                .findById(bulletin.getId());

        //when
        Boolean result = boardService.isBulletin(bulletin.getId());

        //then
        Assertions.assertThat(result).isTrue();
    }

    private Bulletin getBulletin() {
        return Bulletin.builder()
                .user(User.builder().build())
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }
}