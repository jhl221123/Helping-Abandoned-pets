package com.catdog.help.web.form.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter @Setter
public class CommentForm {

    private Long boardId;

    private String nickName;

    @NotBlank
    private String content;

    private LocalDateTime writeDate;
}
