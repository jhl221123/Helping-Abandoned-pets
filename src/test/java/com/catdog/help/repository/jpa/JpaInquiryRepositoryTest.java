package com.catdog.help.repository.jpa;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.User;
import com.catdog.help.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaInquiryRepositoryTest {

    @Autowired JpaInquiryRepository inquiryRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired TestData testData;
    @Autowired EntityManager em;

    @Test
    void 저장_조회_삭제() {
        User user = testData.createUser("user@email", "password", "nickname");
        userRepository.save(user);

        Inquiry inquiry = testData.getInquiry(user, "문의제목", true);

        //저장, 조회
        inquiryRepository.save(inquiry);
        Inquiry findBoard = inquiryRepository.findById(inquiry.getId());

        //조회 결과
        assertThat(findBoard).isEqualTo(inquiry);
        assertThat(findBoard.getSecret()).isEqualTo(true);

        em.flush();
        em.clear();

        //삭제
        Inquiry findBoard2 = inquiryRepository.findById(inquiry.getId());
        inquiryRepository.delete(findBoard2);

        //삭제 결과
        Inquiry deleteBoard = inquiryRepository.findById(inquiry.getId());
        assertThat(deleteBoard).isNull();
    }

    @Test
    void 페이지_조회() throws InterruptedException {
        //given
        User user = testData.createUser("user@email", "password", "nickname");
        userRepository.save(user);

        Inquiry inquiry1 = testData.getInquiry(user, "문의제목1", true);
        Inquiry inquiry2 = testData.getInquiry(user, "문의제목2", true);
        Inquiry inquiry3 = testData.getInquiry(user, "문의제목3", true);
        inquiryRepository.save(inquiry1);
        Thread.sleep(100);
        inquiryRepository.save(inquiry2);
        Thread.sleep(100);
        inquiryRepository.save(inquiry3);

        //when
        List<Inquiry> page = inquiryRepository.findPage(0, 2);

        //then
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.get(0)).isEqualTo(inquiry3);
    }
}