package com.catdog.help.service;

import com.catdog.help.TestData;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class InquiryServiceTest {

    @Autowired InquiryService inquiryService;
    @Autowired UserRepository userRepository;
    @Autowired TestData testData;

    @Test
    void 저장_조회() {
        //given
        User user = testData.createUser("user@email", "password", "nickname");
        userRepository.save(user);

        SaveInquiryForm saveForm = testData.getSaveInquiryForm("nickname", "title", true);

        //when
        Long boardId = inquiryService.saveBoard(saveForm);
        ReadInquiryForm readForm = inquiryService.readForm(boardId);

        //then
        assertThat(readForm.getNickname()).isEqualTo("nickname");
        assertThat(readForm.getTitle()).isEqualTo("title");
        assertThat(readForm.getSecret()).isTrue();
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
        List<PageInquiryForm> pageForms = inquiryService.readPage(0, 2);

        //then
        assertThat(pageForms.size()).isEqualTo(2);
        assertThat(pageForms.get(1).getTitle()).isEqualTo("titleB");
        assertThat(pageForms.get(1).getSecret()).isEqualTo(false);
    }
}