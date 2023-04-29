package com.catdog.help.web.form.inquiry;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class SaveInquiryForm {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Boolean secret;
}
