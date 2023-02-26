package com.catdog.web.controller;

import com.catdog.web.domain.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserForm {

    private String emailId;
    private String password;
    private String name;
    private int age;
    private String gender;
}
