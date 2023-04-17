package com.catdog.help.repository;

import com.catdog.help.TestData;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaLikeBoardRepositoryTest {

    @Autowired JpaLikeBoardRepository jpaLikeBoardRepository;
    @Autowired JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired TestData testData;

    @Test
    void 저장_조회_삭제() {
        //given
        User user1 = testData.createUser("id11@email", "password", "nickname1");
        User user2 = testData.createUser("id22@email", "password", "nickname2");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board = testData.createBulletinBoard("title", user1);
        jpaBulletinBoardRepository.save(board);

        LikeBoard likeBoard = testData.getLikeBoard(board, user1);

        //when
        jpaLikeBoardRepository.save(likeBoard);
        LikeBoard findLikeBoard = jpaLikeBoardRepository.findByIds(board.getId(), user1.getId());

        //then
        assertThat(findLikeBoard).isInstanceOf(LikeBoard.class);

        //delete
        jpaLikeBoardRepository.delete(findLikeBoard);
        LikeBoard deletedLikeBoard = jpaLikeBoardRepository.findById(findLikeBoard.getId());
        assertThat(deletedLikeBoard).isNull();
    }

    @Test
    void 모두조회() {
        //given
        User user1 = testData.createUser("id11@email", "password", "nickname1");
        User user2 = testData.createUser("id22@email", "password", "nickname2");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board1 = testData.createBulletinBoard("title1", user1);
        BulletinBoard board2 = testData.createBulletinBoard("title2", user2);
        jpaBulletinBoardRepository.save(board1);
        jpaBulletinBoardRepository.save(board2);

        jpaLikeBoardRepository.save(testData.getLikeBoard(board1, user1));
        jpaLikeBoardRepository.save(testData.getLikeBoard(board1, user2));
        jpaLikeBoardRepository.save(testData.getLikeBoard(board2, user1));

        //when
////        List<LikeBoard> result1 = likeBoardRepository.countByBoardId(board1.getId());
////        List<LikeBoard> result2 = likeBoardRepository.countByBoardId(board2.getId());
//
//        //then
//        assertThat(result1.size()).isEqualTo(2);
//        assertThat(result2.size()).isEqualTo(1);
    }
}