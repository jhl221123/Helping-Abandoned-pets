package com.catdog.help.repository;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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