package com.catdog.help.repository;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BulletinBoardRepositoryTest {

    @Autowired BulletinBoardRepository bulletinBoardRepository;
    @Autowired UserRepository userRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired LikeBoardRepository likeBoardRepository;

    @Test
    void 저장_단건조회_삭제() {
        /**
         * comment, likeBoard 존재
         */
        //유저 생성
        User user1 = createUser("id1@123", "123");
        userRepository.save(user1);

        //게시글 생성
        BulletinBoard user1Board1 = createBulletinBoard("title1", user1);
        BulletinBoard user1Board2 = createBulletinBoard("title2", user1);
        bulletinBoardRepository.save(user1Board1);
        bulletinBoardRepository.save(user1Board2);

        //댓글 생성
        Comment comment = getComment(user1, user1Board1, "user1이 board1을 작성하다.");
        commentRepository.save(comment);

        //좋아요 생성
        LikeBoard likeBoard = getLikeBoard(user1, user1Board1);
        likeBoardRepository.save(likeBoard);

        //게시글 조회
        BulletinBoard findBoard1 = bulletinBoardRepository.findById(user1Board1.getId());
        BulletinBoard findBoard2 = bulletinBoardRepository.findById(user1Board2.getId());

        //title
        assertThat(findBoard1.getTitle()).isEqualTo("title1");
        assertThat(findBoard2.getTitle()).isEqualTo("title2");

        //region
        assertThat(findBoard1.getRegion()).isEqualTo("region");
        assertThat(findBoard2.getRegion()).isEqualTo("region");

        //user
        assertThat(findBoard1.getUser().getEmailId()).isEqualTo("id1@123");

        //comment
        assertThat(findBoard1.getComments().stream().findAny()).isEqualTo(1); // TODO: 2023-03-28 게시글 조회 시 유저, 댓글 테이블 다중 조인 후 테스트

        // TODO: 2023-03-25 삭제 테스트 수행
//        assertThat(findBoard1).isEqualTo(board1);
//        assertThat(findBoard2).isEqualTo(board2); todo jdbcTemplate 에서는 유효하지 않은 테스트.. 테스트 방향성을 고민해보자.
    }

    @Test
    void findAll() throws InterruptedException {
        //given
        User user1 = createUser("id1@123", "123");
        User user2 = createUser("id2@123", "123");
        BulletinBoard board1 = createBulletinBoard("title1", user1);
        TimeUnit.SECONDS.sleep(1);
        BulletinBoard board2 = createBulletinBoard("title2", user1);
        TimeUnit.SECONDS.sleep(1);
        BulletinBoard board3 = createBulletinBoard("title3", user2);
        // TODO: 2023-03-09 Board.user -> CASCADE.ALL 이라서 수정필요
        userRepository.save(user1);
        userRepository.save(user2);
        //위 2줄
        bulletinBoardRepository.save(board1);
        bulletinBoardRepository.save(board2);
        bulletinBoardRepository.save(board3);

        //when
        List<BulletinBoard> findBoards = bulletinBoardRepository.findAll();

        //then
        //게시판 생성 날짜 기준으로 내림차순 정렬
        assertThat(findBoards.get(0).getTitle()).isEqualTo("title3");
        assertThat(findBoards.get(1).getTitle()).isEqualTo("title2");
        assertThat(findBoards.get(2).getTitle()).isEqualTo("title1");

        assertThat(findBoards.size()).isEqualTo(3);

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
//        comment.setParent(new Comment()); //jpa 아니면 여기서 error
        comment.setDates(new Dates(LocalDateTime.now(), null, null));

        return comment;
    }

    private static LikeBoard getLikeBoard(User user, BulletinBoard board) {
        LikeBoard likeBoard = new LikeBoard();
        likeBoard.setBoard(board);
        likeBoard.setUser(user);
        return likeBoard;
    }
}