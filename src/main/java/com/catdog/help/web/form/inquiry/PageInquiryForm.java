package com.catdog.help.web.form.inquiry;

import com.catdog.help.domain.board.Inquiry;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class PageInquiryForm {

    private Long id;
    private String nickname;
    private String title;
    private LocalDateTime createdDate;
    private Boolean secret;

    public PageInquiryForm(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.nickname = inquiry.getUser().getNickname();
        this.title = inquiry.getTitle();
        this.createdDate = inquiry.getCreatedDate();
        this.secret = inquiry.getSecret();
    }
}
