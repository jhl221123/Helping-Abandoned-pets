package com.catdog.help.domain.board;

import com.catdog.help.domain.BaseEntity;
import com.catdog.help.domain.user.User;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    //대댓글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Comment> child = new ArrayList<>();


    @Builder
    public Comment(Board board, User user, CommentForm form) {
        this.board = board;
        this.user = user;
        this.content = form.getContent();
    }


    public void addParent(Comment parent) {
        this.parent = parent;
        parent.getChild().add(this);
    }

    public void updateComment(UpdateCommentForm form) {
        this.content = form.getContent();
    }
}
