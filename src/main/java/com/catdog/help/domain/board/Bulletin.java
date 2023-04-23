package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
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
public class Bulletin extends Board {

    private String region; // TODO: 2023-04-17 지역이름 검증, 셀렉트박스

    @OneToMany(mappedBy = "board", cascade = REMOVE)
    private List<LikeBoard> likeBoards;


    @Builder
    public Bulletin(User user, String title, String content, String region) {
        super(user, title, content);
        this.region = region;
    }


    public void updateBoard(String title, String content, String region) {
        super.updateBoard(title, content);
        this.region = region;
    }
}
