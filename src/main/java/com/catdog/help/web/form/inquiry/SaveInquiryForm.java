package com.catdog.help.web.form.inquiry;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SaveInquiryForm {

    private String nickname;

    private String title;

    private String content;

    private Boolean secret;
}
