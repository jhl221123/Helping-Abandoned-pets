package com.catdog.help.repository.jpa;

import com.catdog.help.TestData;
import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.JpaBulletinBoardRepository;
import com.catdog.help.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaBoardRepositoryTest {

    @Autowired JpaBoardRepository boardRepository;
    @Autowired UserRepository userRepository;
    @Autowired
    JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired JpaInquiryRepository inquiryRepository;
    @Autowired TestData testData;
    @Autowired EntityManager em;

    @Test
    void findDtype() {
        //given
        User user = testData.createUser("email@id", "password", "nickName");
        userRepository.save(user);

        BulletinBoard bulletinBoard = testData.createBulletinBoard("title", user);
        jpaBulletinBoardRepository.save(bulletinBoard);

        Inquiry inquiry = testData.getInquiry(user, "title", true);
        inquiryRepository.save(inquiry);

        //when
        Board findBulletin = boardRepository.findById(bulletinBoard.getId());
        Board findInquiry = boardRepository.findById(inquiry.getId());

        //then
        assertThat(findBulletin).isInstanceOf(BulletinBoard.class);
        assertThat(findInquiry).isInstanceOf(Inquiry.class);
    }
}