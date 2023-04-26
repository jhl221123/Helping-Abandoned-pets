package com.catdog.help.repository;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BulletinRepository bulletinRepository;


    @Test
    @DisplayName("댓글 저장")
    void save() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user, "제목");
        bulletinRepository.save(board);

        Comment comment = getComment(user, board, "댓글내용");

        //when
        Comment savedComment = commentRepository.save(comment);

        //then
        assertThat(savedComment.getContent()).isEqualTo(comment.getContent());
    }

    @Test
    @DisplayName("댓글 단건 조회")
    void findById() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user, "제목");
        bulletinRepository.save(board);

        Comment comment = getComment(user, board, "댓글내용");
        commentRepository.save(comment);

        //when
        Comment findComment = commentRepository.findById(comment.getId()).get();

        //then
        assertThat(findComment.getContent()).isEqualTo(comment.getContent());
    }

    @Test
    @DisplayName("해당 게시글에 존재하는 댓글 모두 조회")
    void findByBoardId() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user, "제목");
        bulletinRepository.save(board);

        Comment first = getComment(user, board, "첫 댓글");
        Comment second = getComment(user, board, "두번째 댓글");

        Comment third = getComment(user, board, "첫 댓글의 답글");
        third.addParent(first);

        commentRepository.save(first);
        commentRepository.save(second);
        commentRepository.save(third);

        //when
        List<Comment> comments = commentRepository.findByBoardId(board.getId());

        //then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).getChild().get(0)).isEqualTo(third);
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user, "제목");
        bulletinRepository.save(board);

        Comment comment = getComment(user, board, "댓글내용");
        commentRepository.save(comment);
        assertThat(commentRepository.count()).isEqualTo(1L);

        //when
        commentRepository.delete(comment);

        //then
        assertThat(commentRepository.count()).isEqualTo(0L);
    }


    private Comment getComment(User user, Board board, String content) {
        return Comment.builder()
                .user(user)
                .board(board)
                .content(content)
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