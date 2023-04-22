package com.catdog.help.web.form;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class LoginForm {

    @NotBlank
    @Email
    private String emailId;

    @NotBlank
    @Length(min = 8, max = 16)
    private String password;


    public LoginForm() {
    }

    @Builder
    public LoginForm(String emailId, String password) {
        this.emailId = emailId;
        this.password = password;
    }
}
