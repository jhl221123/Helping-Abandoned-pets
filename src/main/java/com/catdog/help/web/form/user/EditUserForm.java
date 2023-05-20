package com.catdog.help.web.form.user;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.api.request.user.EditUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
public class EditUserForm {

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickname;

    @NotBlank
    @Length(min = 1, max = 10)
    private String name;

    @NotNull
    @Max(150)
    private int age;

    @NotNull
    private Gender gender;


    public EditUserForm(User user) {
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.age = user.getAge();
        this.gender = user.getGender();
    }

    public EditUserForm(EditUserRequest request) {
        this.nickname = request.getNickname();
        this.name = request.getName();
        this.age = request.getAge();
        this.gender = request.getGender();
    }
}
