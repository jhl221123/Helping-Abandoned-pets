package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ViewServiceTest {

    @InjectMocks
    ViewService viewService;

    @Mock
    BoardRepository boardRepository;


    @Test
    @DisplayName("해당 글의 조회수가 0에서 1로 증가")
    void addViews() {
        //given
        User user = getUser("test@test.test", "닉네임");

        Bulletin board = getBulletin(user, "제목");

        doReturn(Optional.ofNullable(board)).when(boardRepository)
                .findById(2L);

        //when
        viewService.addViews(2L);

        //then
        assertThat(board.getViews()).isEqualTo(1);
    }


    private Bulletin getBulletin(User user, String title) {
        return Bulletin.builder()
                .user(user)
                .title(title)
                .content("내용")
                .region("지역")
                .build();
    }

    private User getUser(String emailId, String nickname) {
        return User.builder()
                .emailId(emailId)
                .password("12345678")
                .nickname(nickname)
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}