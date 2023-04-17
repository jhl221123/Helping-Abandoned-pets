package com.catdog.help.service;

import com.catdog.help.TestData;
import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaInquiryRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class InquiryServiceTest {

    @Autowired InquiryService inquiryService;
    @Autowired JpaInquiryRepository inquiryRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired TestData testData;
    @Autowired EntityManager em;

    @Test
    void 저장_조회_삭제() {
        //given
        User user = testData.createUser("user@email", "password", "nickname");
        userRepository.save(user);

        SaveInquiryForm saveForm = testData.getSaveInquiryForm("nickname", "title", true);

        //when
        Long boardId = inquiryService.saveBoard(saveForm);
        ReadInquiryForm readForm = inquiryService.readBoard(boardId);

        //then
        assertThat(readForm.getNickname()).isEqualTo("nickname");
        assertThat(readForm.getTitle()).isEqualTo("title");
        assertThat(readForm.getSecret()).isTrue();

        //삭제
        em.flush();
        em.clear();
        inquiryService.deleteBoard(boardId);
        Inquiry findBoard = inquiryRepository.findById(boardId);

        //삭제 결과
        assertThat(findBoard).isNull();
    }



    @Test
    void 페이지_조회() {
        //given
        User user = testData.createUser("user@email", "password", "nickname");
        userRepository.save(user);

        SaveInquiryForm saveFormA = testData.getSaveInquiryForm("nickname", "titleA", true);
        SaveInquiryForm saveFormB = testData.getSaveInquiryForm("nickname", "titleB", false);
        SaveInquiryForm saveFormC = testData.getSaveInquiryForm("nickname", "titleC", true);
        inquiryService.saveBoard(saveFormA);
        inquiryService.saveBoard(saveFormB);
        inquiryService.saveBoard(saveFormC);

        //when
        List<PageInquiryForm> pageForms = inquiryService.readPage(1);

        //then
        assertThat(pageForms.size()).isEqualTo(3);
        assertThat(pageForms.get(1).getTitle()).isEqualTo("titleB");
        assertThat(pageForms.get(1).getSecret()).isEqualTo(false);
    }
}