package com.catdog.help.repository;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.bulletinboard.BulletinBoardRepository;
import com.catdog.help.repository.user.UserRepository;
import lombok.Builder;
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

    @Autowired
    BulletinBoardRepository bulletinBoardRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void 저장_단건조회_삭제() {
        //given
        User user1 = createUser("id1@123", "123");
        BulletinBoard board1 = createBulletinBoard("title1", user1);
        BulletinBoard board2 = createBulletinBoard("title2", user1);

        //when
        bulletinBoardRepository.save(board1);
        bulletinBoardRepository.save(board2);
        BulletinBoard findBoard1 = bulletinBoardRepository.findOne(board1.getId());
        BulletinBoard findBoard2 = bulletinBoardRepository.findOne(board2.getId());

        //then
        assertThat(findBoard1.getTitle()).isEqualTo("title1");
        assertThat(findBoard2.getTitle()).isEqualTo("title2");
        assertThat(findBoard1.getRegion()).isEqualTo("region");
        assertThat(findBoard2.getRegion()).isEqualTo("region");

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
        board.setDates(Dates.builder().createDate(LocalDateTime.now()).build());
        return board;
    }

    @Builder
    private static User createUser(String emailId, String password) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(Dates.builder().createDate(LocalDateTime.now()).build());
        return user;
    }
}