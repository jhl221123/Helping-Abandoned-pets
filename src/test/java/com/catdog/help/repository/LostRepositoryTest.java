package com.catdog.help.repository;

import com.catdog.help.domain.board.Lost;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LostRepositoryTest {

    @Autowired
    private LostRepository lostRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("닉네임으로 실종글 수 조회")
    void countByNickname() {
        //given
        User user = getUser();
        userRepository.save(user);

        Lost board = getLost(user);
        lostRepository.save(board);

        //when
        Long result = lostRepository.countByNickname(user.getNickname());

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("닉네임으로 실종글 페이지 조회")
    void findByNickname() {
        //given
        setLostList();
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "id");

        //when
        Page<Lost> findBoards = lostRepository.findPageByNickname("닉네임", pageRequest);

        //then
        assertThat(findBoards.getContent().size()).isEqualTo(3);
    }


    private void setLostList() {
        User user = getUser();
        userRepository.save(user);

        for (int i = 1; i <= 5; i++) {
            Lost board = Lost.builder()
                    .user(user)
                    .title("제목" + i)
                    .content("내용" + i)
                    .region("부산")
                    .breed("품종" + i)
                    .lostDate(LocalDateTime.now())
                    .lostPlace("실종장소" + i)
                    .gratuity(100000)
                    .build();
            lostRepository.save(board);
        }
    }

    private Lost getLost(User user) {
        return Lost.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("부산")
                .breed("품종")
                .lostDate(LocalDateTime.now())
                .lostPlace("실종장소")
                .gratuity(100000)
                .build();
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}