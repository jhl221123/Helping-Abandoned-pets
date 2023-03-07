package com.catdog.help.repository;

import com.catdog.help.domain.Gender;
import com.catdog.help.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired UserRepository userRepository;

    @Test
    public void saveAndFindOne() {
        //given
        User user1 = createUser("id1@1", "123", "nickName1");
        User user2 = createUser("id2@2", "123", "nickName2");

        //when
        userRepository.save(user1);
        userRepository.save(user2);
        User findUser1 = userRepository.findById(user1.getId());
        User findUser2 = userRepository.findById(user2.getId());
        User findUser3 = userRepository.findByEmailId(user1.getEmailId());
        User findUser4 = userRepository.findByEmailId(user2.getEmailId());
        User findUser5 = userRepository.findByNickName(user1.getNickName());
        User findUser6 = userRepository.findByNickName(user2.getNickName());

        User findUser7 = userRepository.findByEmailId("id3@3");


        //then
        assertThat(findUser1).isEqualTo(user1);
        assertThat(findUser2).isEqualTo(user2);
        assertThat(findUser3).isEqualTo(user1);
        assertThat(findUser4).isEqualTo(user2);
        assertThat(findUser5).isEqualTo(user1);
        assertThat(findUser6).isEqualTo(user2);
        assertThat(findUser7).isEqualTo(null);
    }

    @Test
    public void findAll() {
        //given
        User user1 = createUser("id1", "123", "nickName1");
        User user2 = createUser("id2", "123", "nickName2");
        User user3 = createUser("id3", "123", "nickName3");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        //when
        List<User> users = userRepository.findAll();

        //then
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0)).isEqualTo(user1);
        assertThat(users.get(1)).isEqualTo(user2);
        assertThat(users.get(2)).isEqualTo(user3);

    }

    private static User createUser(String emailId, String password, String nickName) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setNickName(nickName);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setReliability(0);
        user.setJoinDate(LocalDateTime.now());
        return user;
    }
}