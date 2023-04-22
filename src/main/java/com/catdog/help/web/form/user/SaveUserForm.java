package com.catdog.help.web.form.user;

import com.catdog.help.domain.user.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
@NoArgsConstructor
public class SaveUserForm {

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
    @Length(min = 2, max = 10)
    private String name;

    @NotNull
    @Positive
    private int age;

    @NotNull
    private Gender gender;


    @Builder
    public SaveUserForm(String emailId, String password, String nickname, String name, int age, Gender gender) {
        this.emailId = emailId;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}
