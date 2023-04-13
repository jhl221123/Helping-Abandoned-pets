package com.catdog.help.repository;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaCommentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaCommentRepositoryTest {

    @Autowired
    JpaCommentRepository jpaCommentRepository;
    @Autowired
    JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void saveAndFindByIdAndDelete() {
        //given
        User user1 = createUser("id11@email", "password");
        User user2 = createUser("id22@email", "password");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board = createBulletinBoard("title", user1);
        jpaBulletinBoardRepository.save(board);

        Comment comment = getComment(user2, board, "comment");

        //when
        jpaCommentRepository.save(comment);
        Comment findComment = jpaCommentRepository.findById(comment.getId());

        //then
        assertThat(comment.getContent()).isEqualTo(findComment.getContent());
        assertThat(comment.getUser().getId()).isEqualTo(findComment.getUser().getId());

        //delete
        jpaCommentRepository.delete(comment);
        Comment findCommentAfterDelete = jpaCommentRepository.findById(comment.getId());
        assertThat(findCommentAfterDelete).isNull();
    }

    @Test
    void findAll() {
        //given
        User user1 = createUser("id11@email", "password");
        User user2 = createUser("id22@email", "password");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board1 = createBulletinBoard("title1", user1);
        BulletinBoard board2 = createBulletinBoard("title2", user2);
        jpaBulletinBoardRepository.save(board1);
        jpaBulletinBoardRepository.save(board2);

        Comment comment1 = getComment(user2, board1, "comment1");
        Comment comment2 = getComment(user2, board1, "comment2");
        jpaCommentRepository.save(comment1);
        jpaCommentRepository.save(comment2);

        //when
        Comment comment3 = getComment(user2, board1, "comment3");
        comment3.addParent(comment1);
        jpaCommentRepository.save(comment3);

        List<Comment> comments = jpaCommentRepository.findAll(board1.getId());
        for (Comment comment : comments) {
            System.out.println("comment.getContent() = " + comment.getChild()); // TODO: 2023-03-26 자식 댓글 포함시키기
        }

        List<Comment> noComments = jpaCommentRepository.findAll(board2.getId());

        //then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comment1.getContent()).isEqualTo(comments.get(0).getContent());
        assertThat(comment2.getContent()).isEqualTo(comments.get(1).getContent());
        assertThat(comment3).isNotIn(comments); //부모 댓글만 조회되어야 한다.

        assertThat(noComments).isEmpty();
    }

    private static BulletinBoard createBulletinBoard(String title, User user) {
        BulletinBoard board = new BulletinBoard();
        board.setTitle(title);
        board.setContent("content");
        board.setRegion("region");
        board.setUser(user);
        board.setDates(new Dates(LocalDateTime.now(), null, null));
        return board;
    }

    private static User createUser(String emailId, String password) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        return user;
    }

    private static Comment getComment(User user, BulletinBoard board, String content) {
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setUser(user);
        comment.setContent(content);
//        comment.setParent(new Comment()); jpa 아니면 여기서 error
        comment.setDates(new Dates(LocalDateTime.now(), null, null));

        return comment;
    }
}