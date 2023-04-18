package com.catdog.help.domain.board;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "board_title")
    private String title;

    @Column(name = "board_content")
    private String content;

    @OneToMany(mappedBy = "board", cascade = REMOVE)
    private List<UploadFile> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "board_views")
    private int views;


    public Board(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }


    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addViews() {
        this.views++;
    }
}
