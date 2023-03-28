package com.catdog.help.repository;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
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

    @Autowired
    UserRepository userRepository;

    @Test
    public void saveAndFindOne() {
        //given
        User user1 = createUser("user1@email", "password", "nickName1");
        User user2 = createUser("user2@email", "password", "nickName2");

        //when
        userRepository.save(user1);
        userRepository.save(user2);
        User findUser1 = userRepository.findById(user1.getId());
        User findUser2 = userRepository.findById(user2.getId());
        User findUser3 = userRepository.findByEmailId(user1.getEmailId());
        User findUser4 = userRepository.findByEmailId(user2.getEmailId());
        User findUser5 = userRepository.findByNickName(user1.getNickName());
        User findUser6 = userRepository.findByNickName(user2.getNickName());

        User findUser7 = userRepository.findByEmailId("user3@email");


        //then
        assertThat(findUser1.getEmailId()).isEqualTo(user1.getEmailId());
        assertThat(findUser2.getEmailId()).isEqualTo(user2.getEmailId());
        assertThat(findUser3.getEmailId()).isEqualTo(user1.getEmailId());
        assertThat(findUser4.getEmailId()).isEqualTo(user2.getEmailId());
        assertThat(findUser5.getEmailId()).isEqualTo(user1.getEmailId());
        assertThat(findUser6.getEmailId()).isEqualTo(user2.getEmailId());
        assertThat(findUser7).isEqualTo(null);
    }

    @Test
    public void findAll() {
        //given
        User user1 = createUser("user1@email", "password", "nickName1");
        User user2 = createUser("user2@email", "password", "nickName2");
        User user3 = createUser("user3@email", "password", "nickName3");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        //when
        List<User> users = userRepository.findAll();

        //then
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0).getEmailId()).isEqualTo("user1@email");
        assertThat(users.get(1).getEmailId()).isEqualTo("user2@email");
        assertThat(users.get(2).getEmailId()).isEqualTo("user3@email");

    }

    private static User createUser(String emailId, String password, String nickName) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setNickName(nickName);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        return user;
    }
}