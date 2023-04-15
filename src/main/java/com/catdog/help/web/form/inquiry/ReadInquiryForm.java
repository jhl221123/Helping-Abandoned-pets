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
    private LocalDateTime createDate;
    private Boolean secret;

    public ReadInquiryForm(Inquiry findBoard) {
        this.id = findBoard.getId();
        this.nickname = findBoard.getUser().getNickname();
        this.title = findBoard.getTitle();
        this.content = findBoard.getContent();
        this.createDate = findBoard.getDates().getCreateDate();
        this.secret = findBoard.getSecret();
    }
}
