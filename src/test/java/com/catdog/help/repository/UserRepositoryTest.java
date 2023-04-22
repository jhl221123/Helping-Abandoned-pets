package com.catdog.help.repository;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.Grade;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired UserRepository userRepository;


    @Test
    @DisplayName("사용자 추가")
    void addUser() {
        //given
        User user = getUser("test@test.test", "닉네임");

        //when
        User savedUser = userRepository.save(user);

        //then
        assertThat(savedUser.getId()).isEqualTo(user.getId());
        assertThat(savedUser.getEmailId()).isEqualTo(user.getEmailId());
        assertThat(savedUser.getGrade()).isEqualTo(Grade.BASIC);
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail() {
        //given
        User user = getUser("test@test.test", "닉네임");
        User savedUser = userRepository.save(user);

        //when
        User findUser = userRepository.findByEmailId("test@test.test").get();

        //then
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회")
    void findByNickname() {
        //given
        User user = getUser("test@test.test", "닉네임");
        User savedUser = userRepository.save(user);

        //when
        User findUser = userRepository.findByNickname("닉네임").get();

        //then
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("사용자 삭제")
    void deleteUser() {
        //given
        User user = getUser("test@test.test", "닉네임");
        User savedUser = userRepository.save(user);

        assertThat(userRepository.count()).isEqualTo(1L);

        //when
        userRepository.delete(user);

        //then
        assertThat(userRepository.count()).isEqualTo(0L);
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