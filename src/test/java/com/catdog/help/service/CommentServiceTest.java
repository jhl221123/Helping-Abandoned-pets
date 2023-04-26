package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.CommentNotFoundException;
import com.catdog.help.repository.BoardRepository;
import com.catdog.help.repository.CommentRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.EditCommentForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BoardRepository boardRepository;

    @Mock
    UserRepository userRepository;


    @Test
    @DisplayName("부모 댓글 저장")
    void saveParentComment() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        Comment comment = getComment(user, board, "댓글내용");
        CommentForm form = new CommentForm(comment);

        doReturn(Optional.ofNullable(board)).when(boardRepository)
                .findById(form.getBoardId());

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        when(commentRepository.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());

        //when
        Long id = commentService.save(form, -1L);

        //verify
        verify(boardRepository, times(1)).findById(form.getBoardId());
        verify(userRepository, times(1)).findByNickname("닉네임");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("자식 댓글 저장")
    void saveChildComment() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        Comment comment = getComment(user, board, "댓글내용");
        CommentForm form = new CommentForm(comment);

        doReturn(Optional.ofNullable(board)).when(boardRepository)
                .findById(form.getBoardId());

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        doReturn(Optional.ofNullable(comment)).when(commentRepository)
                .findById(4L);

        when(commentRepository.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());

        //when
        Long id = commentService.save(form, 4L);

        //verify
        verify(boardRepository, times(1)).findById(form.getBoardId());
        verify(userRepository, times(1)).findByNickname("닉네임");
        verify(commentRepository, times(1)).findById(4L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("작성자 닉네임 반환")
    void getWriter() {
        //given
        doReturn("닉네임").when(commentRepository)
                .findNicknameById(1L);

        //when
        String nickname = commentService.getWriter(1L);

        //then
        assertThat(nickname).isEqualTo("닉네임");

        //verify
        verify(commentRepository, times(1)).findNicknameById(1L);
    }

    @Test
    @DisplayName("해당 게시글에 존재하는 모든 댓글 조회")
    void readByBoardId() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        Comment first = getComment(user, board, "첫 댓글");
        Comment second = getComment(user, board, "두번째 댓글");

        List<Comment> comments = new ArrayList<>();
        comments.add(first);
        comments.add(second);

        doReturn(comments).when(commentRepository)
                .findByBoardId(2L);

        //when
        List<CommentForm> forms = commentService.readByBoardId(2L);

        //then
        assertThat(forms.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("댓글 수정 양식 호출")
    void getEditForm() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        Comment comment = getComment(user, board, "댓글내용");

        doReturn(Optional.ofNullable(comment)).when(commentRepository)
                .findById(comment.getId());

        //when
        EditCommentForm form = commentService.getEditForm(comment.getId(), user.getNickname());

        //then
        assertThat(form.getContent()).isEqualTo(comment.getContent());
    }

    @Test
    @DisplayName("댓글 수정")
    void update() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        Comment beforeComment = getComment(user, board, "이전 댓글");

        Comment afterComment = getComment(user, board, "수정 후 댓글");
        EditCommentForm form = new EditCommentForm(afterComment, user.getNickname());

        doReturn(Optional.ofNullable(beforeComment)).when(commentRepository)
                .findById(form.getCommentId());

        //when
        commentService.update(form);

        //then
        assertThat(beforeComment.getContent()).isEqualTo("수정 후 댓글");
    }

    @Test
    @DisplayName("댓글 삭제")
    void delete() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        Comment comment = getComment(user, board, "댓글내용");

        doReturn(Optional.ofNullable(comment)).when(commentRepository)
                .findById(comment.getId());

        //expected
        commentService.delete(comment.getId());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 아이디로 조회 시 예외 발생")
    void commentNotFoundExceptionById() {
        //given
        doReturn(Optional.empty()).when(commentRepository)
                .findById(3L);

        //expected
        Assertions.assertThrows(CommentNotFoundException.class,
                ()-> commentService.delete(3L));
    }


    private Comment getComment(User user, Board board, String content) {
        return Comment.builder()
                .user(user)
                .board(board)
                .content(content)
                .build();
    }

    private Bulletin getBulletin(String title) {
        return Bulletin.builder()
                .user(getUser())
                .title(title)
                .content("내용")
                .region("지역")
                .build();
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}