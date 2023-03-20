package com.catdog.help.web.form.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class ChangePasswordForm {

    @NotBlank
    @Length(min = 8, max = 16)
    private String beforePassword;

    @NotBlank
    @Length(min = 8, max = 16)
    private String afterPassword;

    @NotBlank
    @Length(min = 8, max = 16)
    private String checkPassword;
}
