package com.catdog.help.web.api.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.api.response.SaveBulletinResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bulletins")
public class BulletinApiController {

    private final BulletinService bulletinService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;
    private final BoardService boardService;


    /**    create    **/
    @PostMapping("/new")
    public SaveBulletinResponse save(@SessionAttribute(SessionConst.LOGIN_USER) String nickname,
                                     @Validated SaveBulletinForm form, BindingResult bindingResult) {
        Long boardId = bulletinService.save(form, nickname);
        return SaveBulletinResponse.builder()
                .id(boardId)
                .build();
    }
}
