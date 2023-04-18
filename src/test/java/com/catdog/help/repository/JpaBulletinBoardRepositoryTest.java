package com.catdog.help.repository;

import com.catdog.help.TestData;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaCommentRepository;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaBulletinBoardRepositoryTest {

    @Autowired JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired JpaCommentRepository jpaCommentRepository;
    @Autowired JpaLikeBoardRepository jpaLikeBoardRepository;
    @Autowired TestData testData;

    @Test
    void 저장_단건조회_삭제() {
        /**
         * comment, likeBoard 존재
         */
        //유저 생성
        User user1 = testData.createUser("id1@123", "123", "nickname1");
        userRepository.save(user1);

        //게시글 생성
        BulletinBoard user1Board1 = testData.createBulletinBoard("title1", user1);
        BulletinBoard user1Board2 = testData.createBulletinBoard("title2", user1);
        jpaBulletinBoardRepository.save(user1Board1);
        jpaBulletinBoardRepository.save(user1Board2);

        //댓글 생성
        Comment comment = testData.getComment(user1, user1Board1, "user1이 board1에 작성하다.");
        jpaCommentRepository.save(comment);

        //좋아요 생성
        LikeBoard likeBoard = testData.getLikeBoard(user1Board1, user1);
        jpaLikeBoardRepository.save(likeBoard);

        //게시글 조회
        BulletinBoard findBoard1 = jpaBulletinBoardRepository.findById(user1Board1.getId());
        BulletinBoard findBoard2 = jpaBulletinBoardRepository.findById(user1Board2.getId());

        //title
        assertThat(findBoard1.getTitle()).isEqualTo("title1");
        assertThat(findBoard2.getTitle()).isEqualTo("title2");

        //region
        assertThat(findBoard1.getRegion()).isEqualTo("region");
        assertThat(findBoard2.getRegion()).isEqualTo("region");

        //user
        assertThat(findBoard1.getUser().getEmailId()).isEqualTo("id1@123");

        // TODO: 2023-03-25 삭제 테스트 수행
//        assertThat(findBoard1).isEqualTo(board1);
//        assertThat(findBoard2).isEqualTo(board2); todo jdbcTemplate 에서는 유효하지 않은 테스트.. 테스트 방향성을 고민해보자.
    }

    @Test
    void findAll() throws InterruptedException {
        //given
        User user1 = testData.createUser("id1@123", "123", "nickname1");
        User user2 = testData.createUser("id2@123", "123", "nickname2");
        BulletinBoard board1 = testData.createBulletinBoard("title1", user1);
        TimeUnit.SECONDS.sleep(1);
        BulletinBoard board2 = testData.createBulletinBoard("title2", user1);
        TimeUnit.SECONDS.sleep(1);
        BulletinBoard board3 = testData.createBulletinBoard("title3", user2);
        // TODO: 2023-03-09 Board.user -> CASCADE.ALL 이라서 수정필요
        userRepository.save(user1);
        userRepository.save(user2);
        //위 2줄
        jpaBulletinBoardRepository.save(board1);
        jpaBulletinBoardRepository.save(board2);
        jpaBulletinBoardRepository.save(board3);

        //when
        List<BulletinBoard> findBoards = jpaBulletinBoardRepository.findAll();

        //then
        //게시판 생성 날짜 기준으로 내림차순 정렬
        assertThat(findBoards.get(0).getTitle()).isEqualTo("title3");
        assertThat(findBoards.get(1).getTitle()).isEqualTo("title2");
        assertThat(findBoards.get(2).getTitle()).isEqualTo("title1");

        assertThat(findBoards.size()).isEqualTo(3);

    }
}