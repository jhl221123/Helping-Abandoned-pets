package com.catdog.help.web.form.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class UpdateCommentForm {

    private Long commentId; //id(x) 게시글 조회 시 충돌
    private String nickname;
    private String content;
}
