package com.catdog.help.web.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
public class SaveUserForm {

    @NotBlank
    @Email
    private String emailId;

    @NotBlank
    @Length(min = 8, max = 16)
    private String password;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private int age;

    @NotNull
    private String gender;
}
