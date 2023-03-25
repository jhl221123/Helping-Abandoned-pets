package com.catdog.help.domain.board;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Getter @Setter
public class Comment {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "comment_content")
    private String content;

    @Embedded
    private Dates dates;

    //대댓글
    @ManyToOne(fetch = FetchType.LAZY) //fetch join으로 즉시로딩 효과
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Comment> child = new ArrayList<>();

    //===== 연관 관계 편의 메서드 =====//
    public void addParent(Comment parent) {
        this.setParent(parent);
        parent.getChild().add(this);
    }

    /**CAMD 생성 접근 수정 삭제 시간추가*/
}
