package com.catdog.help.web.form.inquiry;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class SaveInquiryForm {

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Boolean secret;
}
