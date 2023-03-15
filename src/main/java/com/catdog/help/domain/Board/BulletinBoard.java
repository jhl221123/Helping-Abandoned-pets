package com.catdog.help.domain.Board;

import com.catdog.help.domain.LikeBoard;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Bulletin")
@Getter @Setter
public class BulletinBoard extends Board {

    private String region;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<UploadFile> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeUsers;


    /**
     * score 보류
     */
    @Column(name = "board_score")
    private int score;
    
//    public void plusScore() {
//        ++score;
//    }
//
//    public void minusScore() {
//        --score;
//    }


    //===== 연관 관계 편의 메서드 =====//

    public void addImage(UploadFile uploadFile) {
        uploadFile.setBoard(this);
        this.getImages().add(uploadFile);
    }
}
