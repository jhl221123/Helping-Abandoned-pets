package com.catdog.help.web.form.user;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class ReadUserForm {

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
    private String nickname;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private int age;

    @NotNull
    private Gender gender;

    @NotNull
    private Dates dates;

    public ReadUserForm(User user) {
        this.id = user.getId();
        this.emailId = user.getEmailId();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.dates = user.getDates();
    }
}
