package com.catdog.help.web.api.request.user;

import com.catdog.help.domain.user.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class EditUserRequest {

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


    @Builder
    public EditUserRequest(String nickname, String name, int age, Gender gender) {
        this.nickname = nickname;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}
