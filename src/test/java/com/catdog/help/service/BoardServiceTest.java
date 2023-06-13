package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.BoardRepository;
import com.catdog.help.web.form.BoardByRegion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    @DisplayName("해당 글의 유형이 실종글인지 확인")
    void isLost() {
        //given
        Lost lost = getLost();

        doReturn(Optional.of(lost)).when(boardRepository)
                .findById(2L);

        //when
        Boolean result = boardService.isLost(2L);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("해당 글의 유형이 게시글인지 확인")
    void isBulletin() {
        //given
        Bulletin bulletin = getBulletin();

        doReturn(Optional.of(bulletin)).when(boardRepository)
                .findById(2L);

        //when
        Boolean result = boardService.isBulletin(2L);

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

    @Test
    @DisplayName("지역별 실종글 수 조회 성공")
    void countLostByRegion() {
        //given
        List<BoardByRegion> boardByRegions = getBoardByRegions();
        doReturn(boardByRegions).when(boardRepository)
                .countLostByRegion();

        //when
        List<BoardByRegion> result = boardService.getLostByRegion();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("지역별 게시글 수 조회 성공")
    void countBulletinByRegion() {
        //given
        List<BoardByRegion> boardByRegions = getBoardByRegions();
        doReturn(boardByRegions).when(boardRepository)
                .countBulletinByRegion();

        //when
        List<BoardByRegion> result = boardService.getBulletinByRegion();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("지역별 나눔글 수 조회 성공")
    void countItemByRegion() {
        //given
        List<BoardByRegion> boardByRegions = getBoardByRegions();
        doReturn(boardByRegions).when(boardRepository)
                .countItemByRegion();

        //when
        List<BoardByRegion> result = boardService.getItemByRegion();

        //then
        assertThat(result.size()).isEqualTo(1);
    }


    private List<BoardByRegion> getBoardByRegions() {
        BoardByRegion boardByRegion = new BoardByRegion("부산", 1L);
        List<BoardByRegion> boardByRegions = new ArrayList<>();
        boardByRegions.add(boardByRegion);
        return boardByRegions;
    }

    private Lost getLost() {
        return Lost.builder()
                .title("제목")
                .content("내용")
                .region("지역")
                .user(User.builder().build())
                .breed("품종")
                .lostDate(LocalDate.now())
                .lostPlace("실종장소")
                .gratuity(10000)
                .build();
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