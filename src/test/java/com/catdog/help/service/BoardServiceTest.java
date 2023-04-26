package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("작성자 닉네임 반환")
    void getWriter() {
        //given
        doReturn("닉네임").when(boardRepository)
                .findNicknameById(1L);

        //when
        String nickname = boardService.getWriter(1L);

        //then
        assertThat(nickname).isEqualTo("닉네임");
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