package com.catdog.help.domain.board;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "comment_content")
    private String content;

    //대댓글
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = REMOVE)
    private List<Comment> child = new ArrayList<>();


    @Builder
    public Comment(User user, Board board, String content) {
        this.board = board;
        this.user = user;
        this.content = content;
    }


    public void addParent(Comment parent) {
        this.parent = parent;
        parent.getChild().add(this);
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
