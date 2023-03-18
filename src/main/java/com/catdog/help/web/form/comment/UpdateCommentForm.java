package com.catdog.help.web.form.comment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCommentForm {

    private Long id;
    private String nickName;
    private String content;
}
