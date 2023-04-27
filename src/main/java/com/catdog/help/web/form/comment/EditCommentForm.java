package com.catdog.help.web.form.comment;

import com.catdog.help.domain.board.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@ToString
public class EditCommentForm {

    private Long commentId; //id(x) 게시글 조회 시 충돌
    private String nickname;

    @NotBlank
    @Length(max = 100)
    private String content;

    public EditCommentForm() { //컨트롤러 파라미터 바인딩
    }

    public EditCommentForm(Comment comment, String nickName) {
        this.commentId = comment.getId();
        this.nickname = nickName;
        this.content = comment.getContent();
    }
}
