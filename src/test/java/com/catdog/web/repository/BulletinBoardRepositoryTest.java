package com.catdog.web.repository;

import com.catdog.web.domain.Board.BulletinBoard;
import com.catdog.web.domain.Gender;
import com.catdog.web.domain.User;
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

    @Test
    void saveAndFindOne() {
        //given
        User user1 = createUser("id1@123", "123");
        BulletinBoard board1 = createBulletinBoard("title1", user1);
        BulletinBoard board2 = createBulletinBoard("title2", user1);

        //when
        bulletinBoardRepository.save(board1);
        bulletinBoardRepository.save(board2);
        BulletinBoard findBoard1 = bulletinBoardRepository.findOne(board1.getNo());
        BulletinBoard findBoard2 = bulletinBoardRepository.findOne(board2.getNo());

        //then
        assertThat(findBoard1.getTitle()).isEqualTo("title1");
        assertThat(findBoard2.getTitle()).isEqualTo("title2");
        assertThat(findBoard1).isEqualTo(board1);
        assertThat(findBoard2).isEqualTo(board2);
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
        //로그인 기능 구현 후 Board.user -> CASCADE.ALL 설정하고 나서 수정하기
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
        assertThat(findBoards.get(0)).isEqualTo(board3);
        assertThat(findBoards.get(1)).isEqualTo(board2);
        assertThat(findBoards.get(2)).isEqualTo(board1);

        assertThat(findBoards.size()).isEqualTo(3);

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
}