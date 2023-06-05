package com.catdog.help.web.api.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.api.request.bulletin.SaveBulletinRequest;
import com.catdog.help.web.api.response.bulletin.PageBulletinResponse;
import com.catdog.help.web.api.response.bulletin.ReadBulletinResponse;
import com.catdog.help.web.api.response.bulletin.SaveBulletinResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.search.BulletinSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static org.springframework.data.domain.Sort.Direction.DESC;

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

    /***  read  ***/
    @GetMapping
    public PageBulletinResponse getPage(@RequestBody BulletinSearch search,
                                        @PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        Page<PageBulletinForm> pageForms = bulletinService.search(search, pageable);
        return PageBulletinResponse.builder()
                .content(pageForms.getContent())
                .page(pageForms.getPageable().getPageNumber())
                .size(pageForms.getPageable().getPageSize())
                .totalElements(pageForms.getTotalElements())
                .totalPages(pageForms.getTotalPages())
                .build();
    }

    @GetMapping("/{id}")
    public ReadBulletinResponse readBoard(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname,
                                          HttpServletRequest request, HttpServletResponse response) {
        //조회수 증가
        viewUpdater.addView(id, request, response);

        ReadBulletinForm readForm = bulletinService.read(id);
        boolean checkLike = likeService.isLike(id, nickname);
        List<CommentForm> commentForms = commentService.readByBoardId(id);
        return ReadBulletinResponse.builder()
                .form(readForm)
                .checkLike(checkLike)
                .commentForms(commentForms)
                .build();
    }

    /***  update  ***/
    @PostMapping("/{id}/like")
    public void clickLike(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname) {
        likeService.clickLike(id, nickname);
    }
}
