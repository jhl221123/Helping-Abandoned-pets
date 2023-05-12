package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Like;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.BoardRepository;
import com.catdog.help.repository.LikeRepository;
import com.catdog.help.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    LikeService likeService;

    @Mock
    LikeRepository likeRepository;

    @Mock
    BoardRepository boardRepository;

    @Mock
    UserRepository userRepository;


    @Test
    @DisplayName("좋아요 클릭한 게시글 조회 결과")
    void userLiked() {
        //given
        User user = getUser("test@test.test", "닉네임");
        Bulletin board = getBulletin(user, "제목");
        Like like = getLike(user, board);

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        doReturn(Optional.ofNullable(like)).when(likeRepository)
                .findByIds(board.getId(), user.getId());

        //when
        boolean result = likeService.isLike(board.getId(), "닉네임");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("좋아요 클릭하지 않은 게시글 조회 결과")
    void userNotLiked() {
        //given
        User user = getUser("test@test.test", "닉네임");
        Bulletin board = getBulletin(user, "제목");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        doReturn(Optional.empty()).when(likeRepository)
                .findByIds(board.getId(), user.getId());

        //when
        boolean result = likeService.isLike(board.getId(), "닉네임");

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("좋아요 버튼 누르면 좋아요 생성")
    void clickLike() {
        //given
        Optional<Like> empty = Optional.empty();
        User user = getUser("test@test.test", "닉네임");
        Bulletin board = getBulletin(user, "제목");

        doReturn(Optional.ofNullable(board)).when(boardRepository)
                .findById(board.getId());

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname(user.getNickname());

        doReturn(empty).when(likeRepository)
                .findByIds(board.getId(), user.getId());

        when(likeRepository.save(any(Like.class))).then(AdditionalAnswers.returnsFirstArg());

        //expected
        likeService.clickLike(board.getId(), "닉네임");
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("좋아요 버튼을 한 번더 누르면 좋아요 해제")
    void clearLike() {
        //given
        User user = getUser("test@test.test", "닉네임");
        Bulletin board = getBulletin(user, "제목");
        Like like = getLike(user, board);

        doReturn(Optional.ofNullable(board)).when(boardRepository)
                .findById(board.getId());

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname(user.getNickname());

        doReturn(Optional.ofNullable(like)).when(likeRepository)
                .findByIds(board.getId(), user.getId());

        doNothing().when(likeRepository)
                .delete(like);

        //expected
        likeService.clickLike(board.getId(), "닉네임");
        verify(likeRepository, times(1)).delete(like);
    }


    private Like getLike(User user, Bulletin board) {
        return Like.builder()
                .user(user)
                .board(board)
                .build();
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