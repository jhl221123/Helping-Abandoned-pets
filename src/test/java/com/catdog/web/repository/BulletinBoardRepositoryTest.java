//package com.catdog.web.repository;
//
//import com.catdog.web.domain.BulletinBoard;
//import com.catdog.web.domain.Gender;
//import com.catdog.web.domain.User;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class BulletinBoardRepositoryTest {
//
//    @Autowired BulletinBoardRepository bulletinBoardRepository;
//
//    @Test
//    void save() {
//        //given
//        BulletinBoard board = createBulletinBoard("id", "title");
//
//        //when
//        bulletinBoardRepository.save(board);
//
//        //then
//        BulletinBoard findBoard = bulletinBoardRepository.findOne(board.getNo());
//        assertThat(findBoard).isEqualTo(board);
//    }
//
//    private static BulletinBoard createBulletinBoard(String userId, String title) {
//        BulletinBoard board = new BulletinBoard();
//        board.setTitle(title);
//        board.setContent("content");
//        board.setRegion("region");
//        board.setUser(createUser(userId, "password"));
//        board.setScore(0);
//        board.setWriteDate(LocalDateTime.now());
//        return board;
//    }
//
//    private static User createUser(String id, String password) {
//        User user = new User();
//        user.setId(id);
//        user.setPassword(password);
//        user.setName("name");
//        user.setAge(28);
//        user.setGender(Gender.MAN);
//        user.setEmail("www.com");
//        user.setPhoneNumber("010-1234-1234");
//        user.setJoinDate(LocalDateTime.now());
//        user.setReliability(0);
//        return user;
//    }
//}