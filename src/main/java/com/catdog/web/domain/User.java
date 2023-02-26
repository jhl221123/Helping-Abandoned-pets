package com.catdog.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue
    @Column(name = "user_no")
    private Long no;

    @Column(name = "user_id", unique = true)
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
    private LocalDateTime joinDate; // 리팩토링 필요
}
