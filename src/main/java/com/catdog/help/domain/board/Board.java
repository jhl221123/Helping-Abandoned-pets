package com.catdog.help.domain.board;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "board_title")
    private String title;

    @Column(name = "board_content")
    private String content;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Embedded
    private Dates dates;

    @Column(name = "board_views")
    private int views;


    public Board(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.dates = new Dates(LocalDateTime.now(), LocalDateTime.now(), null);
    }


    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
        this.dates = new Dates(this.dates.getCreateDate(), LocalDateTime.now(), null);
    }

    public void addViews() {
        this.views++;
    }
}
