package com.catdog.help.repository;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaLikeBoardRepositoryTest {

    @Autowired
    JpaLikeBoardRepository jpaLikeBoardRepository;
    @Autowired
    JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired UserRepository userRepository;

    @Test
    void 저장_조회_삭제() {
        //given
        User user1 = createUser("id11@email", "password");
        User user2 = createUser("id22@email", "password");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board = createBulletinBoard("title", user1);
        jpaBulletinBoardRepository.save(board);

        LikeBoard likeBoard = getLikeBoard(user1, board);

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
        User user1 = createUser("id11@email", "password");
        User user2 = createUser("id22@email", "password");
        userRepository.save(user1);
        userRepository.save(user2);

        BulletinBoard board1 = createBulletinBoard("title1", user1);
        BulletinBoard board2 = createBulletinBoard("title2", user2);
        jpaBulletinBoardRepository.save(board1);
        jpaBulletinBoardRepository.save(board2);

        jpaLikeBoardRepository.save(getLikeBoard(user1, board1));
        jpaLikeBoardRepository.save(getLikeBoard(user2, board1));
        jpaLikeBoardRepository.save(getLikeBoard(user1, board2));

        //when
////        List<LikeBoard> result1 = likeBoardRepository.countByBoardId(board1.getId());
////        List<LikeBoard> result2 = likeBoardRepository.countByBoardId(board2.getId());
//
//        //then
//        assertThat(result1.size()).isEqualTo(2);
//        assertThat(result2.size()).isEqualTo(1);
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

    private static LikeBoard getLikeBoard(User user, BulletinBoard board) {
        LikeBoard likeBoard = new LikeBoard();
        likeBoard.setBoard(board);
        likeBoard.setUser(user);
        return likeBoard;
    }
}