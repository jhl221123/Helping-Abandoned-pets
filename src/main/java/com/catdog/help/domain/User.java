package com.catdog.help.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_emailId", unique = true)
    private String emailId;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_age")
    private int age;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_gender")
    private Gender gender;

    @Column(name = "user_reliability")
    private int reliability;

    @Column(name = "user_join_date")
    private LocalDateTime joinDate; //todo 2023-03-05 날짜 모음 값타입으로 수정
}
