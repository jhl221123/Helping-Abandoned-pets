package com.catdog.help.repository;

import com.catdog.help.TestData;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaCommentRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
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

    @Autowired JpaCommentRepository jpaCommentRepository;
    @Autowired JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired TestData testData;

    @Test
    void saveAndFindByIdAndDelete() {
        //given
        User user1 = testData.createUser("id11@email", "password", "nickname1");
        User user2 = testData.createUser("id22@email", "password", "nickname2");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board = testData.createBulletinBoard("title", user1);
        jpaBulletinBoardRepository.save(board);

        Comment comment = testData.getComment(user2, board, "comment");

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
        User user1 = testData.createUser("id11@email", "password", "nickname1");
        User user2 = testData.createUser("id22@email", "password", "nickname2");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board1 = testData.createBulletinBoard("title1", user1);
        BulletinBoard board2 = testData.createBulletinBoard("title2", user2);
        jpaBulletinBoardRepository.save(board1);
        jpaBulletinBoardRepository.save(board2);

        Comment comment1 = testData.getComment(user2, board1, "comment1");
        Comment comment2 = testData.getComment(user2, board1, "comment2");
        jpaCommentRepository.save(comment1);
        jpaCommentRepository.save(comment2);

        //when
        Comment comment3 = testData.getComment(user2, board1, "comment3");
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
}