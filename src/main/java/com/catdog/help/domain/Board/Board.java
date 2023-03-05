package com.catdog.help.domain.Board;

import com.catdog.help.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Board {

    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @Column(name = "board_title")
    private String title;

    @Column(name = "board_content")
    private String content;

    @Column(name = "board_date")
    private LocalDateTime writeDate; //날짜모음 값타입으로 리팩토링 필요
}
