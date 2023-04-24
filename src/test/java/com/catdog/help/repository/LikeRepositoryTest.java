package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Like;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LikeRepositoryTest {

    @Autowired LikeRepository likeRepository;

    @Autowired BulletinRepository bulletinRepository;

    @Autowired UserRepository userRepository;


    @Test
    @DisplayName("사용자가 게시글에 좋아요 버튼 누르면 좋아요 생성")
    void save() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user, "제목");
        bulletinRepository.save(board);

        Like like = getLike(user, board);

        //when
        Like savedLike = likeRepository.save(like);

        //then
        assertThat(savedLike).isEqualTo(like);
    }

    @Test
    @DisplayName("게시글, 사용자 아이디로 해당 좋아요 데이터 조회")
    void findByIds() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user, "제목");
        bulletinRepository.save(board);

        Like like = getLike(user, board);
        likeRepository.save(like);

        //when
        Like findLike = likeRepository.findByIds(board.getId(), user.getId()).get();

        //then
        assertThat(findLike.getUser()).isEqualTo(user);
        assertThat(findLike.getBoard()).isEqualTo(board);
    }

    @Test
    @DisplayName("해당 게시글의 좋아요 수 조회")
    void countByBoardId() {
        //given
        User userA = getUser("A@test.test", "A");
        User userB = getUser("B@test.test", "B");
        userRepository.save(userA);
        userRepository.save(userB);

        Bulletin board = getBulletin(userA, "제목");
        bulletinRepository.save(board);

        Like likeA = getLike(userA, board);
        Like likeB = getLike(userB, board);
        likeRepository.save(likeA);
        likeRepository.save(likeB);

        //when
        Long likeOfBoard = likeRepository.countByBoardId(board.getId());

        //then
        assertThat(likeOfBoard).isEqualTo(2L);
    }

    @Test
    @DisplayName("좋아요 삭제")
    void delete() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user, "제목");
        bulletinRepository.save(board);

        Like like = getLike(user, board);
        likeRepository.save(like);

        assertThat(likeRepository.count()).isEqualTo(1L);

        //when
        likeRepository.delete(like);

        //then
        assertThat(likeRepository.count()).isEqualTo(0L);
    }


    private Like getLike(User user, Bulletin board) {
        return Like.builder()
                .board(board)
                .user(user)
                .build();
    }

    private Bulletin getBulletin(User user, String title) {
        return Bulletin.builder()
                .user(user)
                .title(title)
                .content("내용")
                .region("지역")
                .build();
    }

    private User getUser(String emailId, String nickname) {
        return User.builder()
                .emailId(emailId)
                .password("12345678")
                .nickname(nickname)
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}