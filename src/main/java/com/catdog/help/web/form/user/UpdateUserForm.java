package com.catdog.help.web.form.user;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class UpdateUserForm {

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickname;

    @NotBlank
    private String name;

    @NotNull
    private int age;

    @NotNull
    private Gender gender;

    public UpdateUserForm() {
    }

    public UpdateUserForm(User user) {
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.age = user.getAge();
        this.gender = user.getGender();
    }
}
