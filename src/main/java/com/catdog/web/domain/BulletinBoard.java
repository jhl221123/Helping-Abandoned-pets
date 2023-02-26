package com.catdog.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class BulletinBoard {

    @Id @GeneratedValue
    @Column(name = "board_no")
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    private String region;

    @Column(name = "board_title")
    private String title;

    @Column(name = "board_content")
    private String content;

    @Column(name = "board_image")
    private String image; // Blob으로 교체!

    @Column(name = "board_date")
    private LocalDateTime writeDate; //리팩토링 필요

    @Column(name = "board_score")
    private int score;
}
