package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("Bulletin")
public class BulletinBoard extends Board {

    private String region; // TODO: 2023-04-17 지역이름 검증, 셀렉트박스

    @OneToMany(mappedBy = "board", cascade = REMOVE)
    private List<LikeBoard> likeBoards;


    @Builder
    public BulletinBoard(User user, SaveBulletinBoardForm form) {
        super(user, form.getTitle(), form.getContent());
        this.region = form.getRegion();
    }


    public void updateBoard(UpdateBulletinBoardForm form) {
        super.updateBoard(form.getTitle(), form.getContent());
        this.region = form.getRegion();
    }
}
