package com.catdog.help.web.api.response.user;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.web.form.user.ReadUserForm;
import lombok.Getter;

@Getter
public class ReadUserResponse {

    private Long id;

    private String emailId;

    private String password;

    private String nickname;

    private String name;

    private int age;

    private Gender gender;


    public ReadUserResponse(ReadUserForm form) {
        this.id = form.getId();
        this.emailId = form.getEmailId();
        this.password = form.getPassword();
        this.nickname = form.getNickname();
        this.name = form.getName();
        this.age = form.getAge();
        this.gender = form.getGender();
    }
}
