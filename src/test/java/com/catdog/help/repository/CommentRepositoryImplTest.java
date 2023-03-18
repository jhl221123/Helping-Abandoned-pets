package com.catdog.help.repository;

import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentRepositoryImplTest {

    @Autowired CommentRepository commentRepository;
    @Autowired BulletinBoardRepository bulletinBoardRepository;
    @Autowired UserRepository userRepository;

    @Test
    void saveAndFindByIdAndDelete() {
        //given
        User user1 = createUser("id11@email", "password");
        User user2 = createUser("id22@email", "password");
        BulletinBoard board = createBulletinBoard("title", user1);

        Comment comment = getComment(user2, board, "comment");

        //when
        commentRepository.save(comment);
        Comment findComment = commentRepository.findById(comment.getId());

        //then
        assertThat(comment).isEqualTo(findComment);

        //delete
        commentRepository.delete(comment);
        Comment findCommentAfterDelete = commentRepository.findById(comment.getId());
        assertThat(findCommentAfterDelete).isNull();
    }

    @Test
    void findAll() {
        //given
        User user1 = createUser("id11@email", "password");
        User user2 = createUser("id22@email", "password");
        userRepository.save(user1);
        userRepository.save(user2);
        BulletinBoard board = createBulletinBoard("title", user1);
        bulletinBoardRepository.save(board);

        Comment comment1 = getComment(user2, board, "comment1");
        Comment comment2 = getComment(user2, board, "comment2");
        Comment comment3 = getComment(user2, board, "comment3");
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        comment3.addParent(comment1);

        //when
        List<Comment> comments = commentRepository.findAll(board.getId());
        for (Comment comment : comments) {
            System.out.println("comment.getContent() = " + comment.getChild());
        }

        //then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comment1).isIn(comments);
        assertThat(comment2).isIn(comments);
        assertThat(comment3).isNotIn(comments);
    }

    private static BulletinBoard createBulletinBoard(String title, User user) {
        BulletinBoard board = new BulletinBoard();
        board.setTitle(title);
        board.setContent("content");
        board.setRegion("region");
        board.setUser(user);
        board.setScore(0);
        board.setWriteDate(LocalDateTime.now());
        return board;
    }

    private static User createUser(String emailId, String password) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setReliability(0);
        user.setJoinDate(LocalDateTime.now());
        return user;
    }

    private static Comment getComment(User user2, BulletinBoard board, String content) {
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setUser(user2);
        comment.setContent(content);

        return comment;
    }
}