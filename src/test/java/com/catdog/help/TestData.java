package com.catdog.help;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestData {

    /** User */

    public User createUser(String emailId, String password, String nickname) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setNickName(nickname);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        return user;
    }


    /** BulletinBoard */

    public BulletinBoard createBulletinBoard(String title, User user) {
        BulletinBoard board = new BulletinBoard();
        board.setTitle(title);
        board.setContent("content");
        board.setRegion("region");
        board.setUser(user);
        board.setDates(new Dates(LocalDateTime.now(), null, null));
        return board;
    }


    /** inquiry */

    public Inquiry getInquiry(User user, String title, boolean secret) {
        Inquiry inquiry = new Inquiry();
        inquiry.setUser(user);
        inquiry.setTitle(title);
        inquiry.setContent("문의내용");
        inquiry.setDates(new Dates(LocalDateTime.now(), null, null));
        inquiry.setSecret(secret);
        return inquiry;
    }

    public SaveInquiryForm getSaveInquiryForm(String nickname, String title, boolean secret) {
        SaveInquiryForm saveForm = new SaveInquiryForm();
        saveForm.setNickname(nickname);
        saveForm.setTitle(title);
        saveForm.setContent("content");
        saveForm.setSecret(secret);
        return saveForm;
    }
}
