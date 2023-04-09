package com.catdog.help.domain.user;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.board.LikeBoard;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "users") //user -> h2 시스템명
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_email_id", unique = true)
    private String emailId;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_nickname", unique = true)
    private String nickName;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_age")
    private int age;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_gender")
    private Gender gender;

    @Embedded
    private Dates dates;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_grade")
    private Grade grade;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<BulletinBoard> bulletinBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();


    // TODO: 2023-03-25 빌더 더 알아보고 setter 닫기
//    @Builder
//    public User(Long id, String emailId, String password, String nickName, String name, int age, Gender gender,
//                List<LikeBoard> likeBoards, List<Comment> comments, DateList dateList, List<BulletinBoard> bulletinBoards) {
//        this.id = id;
//        this.emailId = emailId;
//        this.password = password;
//        this.nickName = nickName;
//        this.name = name;
//        this.age = age;
//        this.gender = gender;
//        this.likeBoards = likeBoards;
//        this.comments = comments;
//        this.dateList = dateList;
//        this.bulletinBoards = bulletinBoards;
//    }
}
