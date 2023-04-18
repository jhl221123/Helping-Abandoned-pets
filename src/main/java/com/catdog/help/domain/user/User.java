package com.catdog.help.domain.user;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users") //user -> h2 시스템명
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_email_id", unique = true)
    private String emailId;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_nickname", unique = true)
    private String nickname;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_age")
    private int age;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_gender")
    private Gender gender;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_grade")
    private Grade grade;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<BulletinBoard> bulletinBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();


    public User(SaveUserForm form) {
        this.emailId = form.getEmailId();
        this.password = form.getPassword();
        this.nickname = form.getNickname();
        this.name = form.getName();
        this.age = form.getAge();
        this.gender = form.getGender();
        this.grade = Grade.BASIC;
    }


    public void updateUser(UpdateUserForm form) {
        this.name = form.getName();
        this.age = form.getAge();
        this.gender = form.getGender();
    }

    public void changePassword(String afterPassword) {
        this.password = afterPassword;
    }
}
