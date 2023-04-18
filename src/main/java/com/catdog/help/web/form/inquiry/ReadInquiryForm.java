package com.catdog.help.web.form.inquiry;

import com.catdog.help.domain.board.Inquiry;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class ReadInquiryForm {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private Boolean secret;

    public ReadInquiryForm(Inquiry findBoard) {
        this.id = findBoard.getId();
        this.nickname = findBoard.getUser().getNickname();
        this.title = findBoard.getTitle();
        this.content = findBoard.getContent();
        this.createdDate = findBoard.getCreatedDate();
        this.secret = findBoard.getSecret();
    }
}
