package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeBoard {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public LikeBoard(Board board, User user) {
        this.board = board;
        this.user = user;
    }
}
