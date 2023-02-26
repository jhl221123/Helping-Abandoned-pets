//package com.catdog.web.repository;
//
//import com.catdog.web.domain.Gender;
//import com.catdog.web.domain.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class UserRepositoryTest {
//
//    @Autowired UserRepository userRepository;
//
//    @Test
//    public void save() {
//        //given
//        User user = createUser("id", "123");
//
//        //when
//        userRepository.save(user);
//
//        //then
//        User findUser = userRepository.findOne(user.getNo());
//        assertThat(findUser).isEqualTo(user);
//    }
//
//    @Test
//    public void findOne() {
//        //given
//        User user1 = createUser("id1", "123");
//        User user2 = createUser("id2", "123");
//        userRepository.save(user1);
//        userRepository.save(user2);
//
//        //when
//        User findUser1 = userRepository.findOne(user1.getNo());
//        User findUser2 = userRepository.findOne(user2.getNo());
//
//        //then
//        assertThat(findUser1).isEqualTo(user1);
//        assertThat(findUser2).isEqualTo(user2);
//    }
//
//    @Test
//    public void findAll() {
//        //given
//        User user1 = createUser("id1", "123");
//        User user2 = createUser("id2", "123");
//        User user3 = createUser("id3", "123");
//        userRepository.save(user1);
//        userRepository.save(user2);
//        userRepository.save(user3);
//
//        //when
//        List<User> users = userRepository.findAll();
//
//        //then
//        assertThat(users.size()).isEqualTo(3);
//
//    }
//
//    private static User createUser(String id, String password) {
//        User user = new User();
//        user.setId(id);
//        user.setPassword(password);
//        user.setName("name");
//        user.setAge(28);
//        user.setGender(Gender.MAN);
//        user.setEmail("www.com");
//        user.setPhoneNumber("010-1234-1234");
//        user.setJoinDate(LocalDateTime.now());
//        user.setReliability(0);
//        return user;
//    }
//}