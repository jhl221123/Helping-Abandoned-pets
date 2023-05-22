package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Like;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BulletinRepositoryTest {

    @Autowired
    BulletinRepository bulletinRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LikeRepository likeRepository;


    @Test
    @DisplayName("게시글 저장")
    void save() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user);

        //when
        Bulletin savedBoard = bulletinRepository.save(board);

        //then
        assertThat(savedBoard.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("id로 단건 조회")
    void findById() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user);
        bulletinRepository.save(board);

        //when
        Bulletin findBoard = bulletinRepository.findById(board.getId()).get();

        //then
        assertThat(findBoard.getTitle()).isEqualTo(board.getTitle());
    }

    @Test
    @DisplayName("닉네임으로 게시글 수 조회")
    void countByNickname() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user);
        bulletinRepository.save(board);

        //when
        Long result = bulletinRepository.countByNickname(user.getNickname());

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("닉네임으로 게시글 페이지 조회")
    void findByNickname() {
        //given
        setBulletinList();
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Bulletin> findBoards = bulletinRepository.findPageByNickname("닉네임", pageRequest);

        //then
        assertThat(findBoards.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자가 좋아하는 게시글 페이지 조회 성공")
    void findLikeBulletinByNickname() {
        //given
        User user_A = getUser("test@AAAA", "닉네임_A");
        User user_B = getUser("test@BBBB", "닉네임_B");
        userRepository.save(user_A);
        userRepository.save(user_B);

        Bulletin board_A = getBulletin(user_A);
        Bulletin board_B = getBulletin(user_B);
        bulletinRepository.save(board_A);
        bulletinRepository.save(board_B);

        //user_A가 좋아요를 누른 게시글은 2개
        Like like_A = getLike(user_A, board_A);
        Like like_B = getLike(user_A, board_B);
        Like like_C = getLike(user_B, board_B);
        likeRepository.save(like_A);
        likeRepository.save(like_B);
        likeRepository.save(like_C);

        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "board_id");

        //when
        Page<Bulletin> boards = bulletinRepository.findLikeBulletins(user_A.getId(), pageRequest);

        //then
        assertThat(boards.getContent().size()).isEqualTo(2L);
    }

    @Test
    @DisplayName("페이지 조회")
    void findPage() {
        //given
        setBulletinList();
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Bulletin> page = bulletinRepository.findPageBy(pageRequest);

        //then
        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("제목_5");
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() {
        //given
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        Bulletin board = getBulletin(user);
        bulletinRepository.save(board);
        assertThat(bulletinRepository.count()).isEqualTo(1L);

        //when
        bulletinRepository.delete(board);

        //then
        assertThat(bulletinRepository.count()).isEqualTo(0L);
    }


    private Like getLike(User user, Bulletin board) {
        return Like.builder()
                .board(board)
                .user(user)
                .build();
    }

    private Bulletin getBulletin(User user) {
        return Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }

    private void setBulletinList() {
        User user = getUser("test@test.test", "닉네임");
        userRepository.save(user);

        for (int i = 1; i <= 5; i++) {
            Bulletin board = Bulletin.builder()
                    .user(user)
                    .title("제목_" + i)
                    .content("내용_" + i)
                    .region("지역_" + i)
                    .build();
            bulletinRepository.save(board);
        }
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