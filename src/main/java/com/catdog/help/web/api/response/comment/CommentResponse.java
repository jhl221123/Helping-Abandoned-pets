package com.catdog.help.web.api.response.comment;

import com.catdog.help.web.form.comment.CommentForm;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponse {

    private Long id;

    private Long boardId;

    private String nickname;

    @NotBlank
    @Length(max = 100)
    private String content;

    private LocalDateTime createdDate;

    private List<CommentResponse> child = new ArrayList<>();


    @Builder
    public CommentResponse(CommentForm form) {
        this.id = form.getId();
        this.boardId = form.getBoardId();
        this.nickname = form.getNickname();
        this.content = form.getContent();
        this.createdDate = form.getCreatedDate();
        if (!form.getChild().isEmpty()) {
            for (CommentForm child : form.getChild()) {
                CommentResponse childCommentForm = new CommentResponse(child);
                this.child.add(childCommentForm);
            }
        }
    }
}
