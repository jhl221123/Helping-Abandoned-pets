package com.catdog.help.web.form.inquiry;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReadInquiryForm {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private Boolean secret;
}
