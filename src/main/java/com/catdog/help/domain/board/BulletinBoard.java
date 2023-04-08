package com.catdog.help.domain.board;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Bulletin")
@Getter @Setter
public class BulletinBoard extends Board {

    private String region; // TODO: 2023-03-30 지역이름 검증, 셀렉트박스

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<UploadFile> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoards;


    //===== 연관 관계 편의 메서드 =====//

    public void addImage(UploadFile uploadFile) {
        uploadFile.setBoard(this);
        this.getImages().add(uploadFile);
    }
}
