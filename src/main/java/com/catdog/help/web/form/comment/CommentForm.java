package com.catdog.help.web.form.comment;

import com.catdog.help.domain.Dates;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CommentForm {

    private Long id;

    private Long boardId;

    private String nickname;

    @NotBlank
    private String content;

    private List<CommentForm> child = new ArrayList<>();

    private Dates dates;
}
