package com.catdog.web.domain.Board;

import com.catdog.web.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("Bulletin")
@Getter @Setter
public class BulletinBoard extends Board {

    private String region;

    @Column(name = "board_image")
    private String image; // Blob으로 교체!

    @Column(name = "board_score")
    private int score;
}
