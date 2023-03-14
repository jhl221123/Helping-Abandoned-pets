package com.catdog.help.domain.Board;

import com.catdog.help.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class LikeBoard {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    
    //Entity 생성용
    public LikeBoard() {
    }

    public LikeBoard(Board board, User user) {
        this.board = board;
        this.user = user;
    }
}
