package com.catdog.help.web.form.comment;

import com.catdog.help.domain.board.Comment;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CommentForm {

    private Long id;

    private Long boardId;

    private String nickname;

    @NotBlank
    @Length(max = 100)
    private String content;

    private LocalDateTime createdDate;

    private List<CommentForm> child = new ArrayList<>();

    public CommentForm() {
    }

    public CommentForm(Comment comment) {
        this.id = comment.getId();
        this.boardId = comment.getBoard().getId(); // TODO: 2023-04-14 여기 지연로딩이라 개선 필요
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedDate();
        if (!comment.getChild().isEmpty()) {
            for (Comment child : comment.getChild()) {
                CommentForm childCommentForm = new CommentForm(child);
                this.child.add(childCommentForm);
            }
        }
    }
}
