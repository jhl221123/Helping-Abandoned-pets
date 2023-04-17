package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Bulletin")
public class BulletinBoard extends Board {

    private String region; // TODO: 2023-03-30 지역이름 검증, 셀렉트박스

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
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
