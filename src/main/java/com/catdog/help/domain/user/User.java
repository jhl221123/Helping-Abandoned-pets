package com.catdog.help.domain.user;

import com.catdog.help.domain.DateList;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.board.LikeBoard;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoards;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @Column(name = "user_reliability")
    private int reliability;

    @Embedded
    private DateList dateList;

}
