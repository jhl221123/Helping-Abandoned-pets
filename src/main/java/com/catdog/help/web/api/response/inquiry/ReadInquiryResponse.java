package com.catdog.help.web.api.response.inquiry;

import com.catdog.help.domain.board.SecretStatus;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReadInquiryResponse {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private SecretStatus secret;


    public ReadInquiryResponse(ReadInquiryForm form) {
        this.id = form.getId();
        this.nickname = form.getNickname();
        this.title = form.getTitle();
        this.content = form.getContent();
        this.createdDate = form.getCreatedDate();
        this.secret = form.getSecret();
    }
}
