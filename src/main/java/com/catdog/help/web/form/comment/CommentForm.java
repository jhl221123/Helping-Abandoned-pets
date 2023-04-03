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

    private String nickName;

    @NotBlank
    private String content;

    // TODO: 2023-03-30 child 추가!! detail.html에서 조회 안됨!, 역할에 맞게 클래스명 변경 

    private List<CommentForm> child = new ArrayList<>();

    private Dates dates;
}
