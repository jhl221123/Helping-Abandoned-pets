package com.catdog.help.domain.user;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.board.LikeBoard;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users") //user -> h2 시스템명
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
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

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<Board> bulletinBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<LikeBoard> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = REMOVE)
    private List<Comment> comments = new ArrayList<>();


    @Builder
    public User(String emailId, String password, String nickname, String name, int age, Gender gender) {
        this.emailId = emailId;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.grade = Grade.BASIC;
    }


    public void updateUser(String name, int age, Gender gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public void changePassword(String afterPassword) {
        this.password = afterPassword;
    }

    public void makeManager() {
        this.grade = Grade.MANAGER;
    }
}
