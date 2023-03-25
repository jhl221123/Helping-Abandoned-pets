package com.catdog.help.web.dto;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
public class UserDto {

    @NotNull
    private Long id;

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
    private Gender gender;

    @NotNull
    private Dates dates;
}
