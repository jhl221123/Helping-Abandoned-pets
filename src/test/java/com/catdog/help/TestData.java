package com.catdog.help;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import com.catdog.help.web.form.user.SaveUserForm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestData {

    /** User */

    public User createUser(String emailId, String password, String nickname) {
        User user = new User();
        user.setEmailId(emailId);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setName("name");
        user.setAge(28);
        user.setGender(Gender.MAN);
        user.setDates(new Dates(LocalDateTime.now(), null, null));
        return user;
    }


    /** BulletinBoard */

    public BulletinBoard createBulletinBoard(String title, User user) {
        SaveBulletinBoardForm form = new SaveBulletinBoardForm();
        form.setTitle(title);
        form.setContent("content");
        form.setRegion("region");
        BulletinBoard board = new BulletinBoard(user, form);
        return board;
    }


    /** inquiry */

    public Inquiry getInquiry(User user, String title, boolean secret) {
        SaveInquiryForm form = getSaveInquiryForm(user.getNickname(), title, secret);
        Inquiry inquiry = new Inquiry(user, form);
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
