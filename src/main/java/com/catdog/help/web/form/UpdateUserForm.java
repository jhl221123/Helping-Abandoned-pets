package com.catdog.help.web.form;

import com.catdog.help.domain.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class UpdateUserForm {

    @NotNull
    private Long id;

    @NotBlank
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
    private int age;

    @NotNull
    private Gender gender;


}
