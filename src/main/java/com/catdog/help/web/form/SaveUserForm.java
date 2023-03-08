package com.catdog.help.web.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Getter @Setter
public class SaveUserForm {

    @NotBlank
    @Email
    private String emailId;

    @NotBlank
    @Length(min = 8, max = 16)
    private String password;

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickName;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private int age;

    @NotNull
    private String gender;
}
