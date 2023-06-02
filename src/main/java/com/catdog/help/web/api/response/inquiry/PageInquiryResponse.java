package com.catdog.help.web.api.response.inquiry;

import com.catdog.help.web.form.inquiry.PageInquiryForm;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageInquiryResponse {

    private Long id;
    private String nickname;
    private String title;
    private LocalDateTime createdDate;
    private Boolean secret;

    public PageInquiryResponse(PageInquiryForm form) {
        this.id = form.getId();
        this.nickname = form.getNickname();
        this.title = form.getTitle();
        this.createdDate = form.getCreatedDate();
        this.secret = form.getSecret();
    }
}
