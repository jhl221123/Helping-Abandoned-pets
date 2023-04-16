package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("inquiry")
public class Inquiry extends Board{
    private Boolean secret;


    public Inquiry(User user, SaveInquiryForm form) {
        super(user, form.getTitle(), form.getContent());
        this.secret = form.getSecret();
    }


    public void updateBoard(EditInquiryForm form) {
        super.updateBoard(form.getTitle(), form.getContent());
        this.secret = form.getSecret();
    }
}
