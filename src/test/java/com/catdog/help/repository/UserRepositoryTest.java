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
        User user1 = createUser("id1@123", "123");
        User user2 = createUser("id2@123", "123");

        //when
        userRepository.save(user1);
        userRepository.save(user2);
        User findUser1 = userRepository.findOne(user1.getId());
        User findUser2 = userRepository.findOne(user2.getId());

        //then
        assertThat(findUser1.getEmailId()).isEqualTo("id1@123");
        assertThat(findUser2.getEmailId()).isEqualTo("id2@123");
        assertThat(findUser1).isEqualTo(user1);
        assertThat(findUser2).isEqualTo(user2);
    }

    @Test
    public void findAll() {
        //given
        User user1 = createUser("id1", "123");
        User user2 = createUser("id2", "123");
        User user3 = createUser("id3", "123");
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

    private static User createUser(String emailId, String password) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setReliability(0);
        user.setJoinDate(LocalDateTime.now());
        return user;
    }
}