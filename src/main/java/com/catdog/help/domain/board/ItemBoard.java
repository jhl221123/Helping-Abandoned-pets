package com.catdog.help.domain.board;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("Share")
@Getter @Setter
public class ItemBoard extends Board {

    private String itemName;

    private int price;

    @Enumerated(value = EnumType.STRING)
    private ItemStatus status;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<UploadFile> images = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoards = new ArrayList<>();

    //===== 연관 관계 편의 메서드 =====//

    public void addImage(UploadFile uploadFile) {
        uploadFile.setBoard(this);
        this.getImages().add(uploadFile);
    }
}
