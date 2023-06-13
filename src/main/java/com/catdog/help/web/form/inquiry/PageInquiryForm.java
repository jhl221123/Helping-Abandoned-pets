package com.catdog.help.web.form.inquiry;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.board.SecretStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.catdog.help.domain.board.SecretStatus.*;

@Getter @Setter
public class PageInquiryForm {

    private Long id;
    private String nickname;
    private String title;
    private LocalDateTime createdDate;
    private SecretStatus secret;

    public PageInquiryForm(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.nickname = inquiry.getUser().getNickname();
        this.title = inquiry.getTitle();
        this.createdDate = inquiry.getCreatedDate();
        this.secret = inquiry.getSecret();
    }


    public Boolean isSecret() {
        if (this.secret.equals(SECRET)) {
            return true;
        } else return false;
    }
}
