package com.catdog.help.web.api.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.api.request.bulletin.SaveBulletinRequest;
import com.catdog.help.web.api.response.bulletin.SaveBulletinResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    public SaveBulletinResponse save(@RequestBody @Validated SaveBulletinRequest request) {
        Long boardId = bulletinService.save(new SaveBulletinForm(request), request.getNickname());
        return new SaveBulletinResponse(boardId);
    }
}
