package com.catdog.help.web.controller;

import com.catdog.help.service.CommentService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/boards/{id}/comments/parent")
    public String createParentComment(@PathVariable("id") Long boardId, @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName,
                                      @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-17  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }
        commentForm.setBoardId(boardId);
        commentForm.setNickName(nickName);
        commentService.createComment(commentForm, -1L);
        return "redirect:/boards/{id}";
    }

    @PostMapping("/boards/{id}/comments/child")
    public String createChildComment(@PathVariable("id") Long boardId, @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName,
                                     @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult,
                                     @RequestParam("parentId") Long parentId) {
        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-17  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }
        commentForm.setBoardId(boardId);
        commentForm.setNickName(nickName);
        commentService.createComment(commentForm, parentId);
        return "redirect:/boards/{id}";
    }


    @PostMapping("/boards/{id}/comments/{commentId}/update")
    public String updateComment(@PathVariable("commentId") Long commentId, @PathVariable("id") Long boardId,
                                @Validated @ModelAttribute("updateCommentForm") UpdateCommentForm updateForm, BindingResult bindingResult,
                                @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        //본인 댓글만 수정 가능
        String commentNickName = commentService.readComment(commentId).getNickName();
        if (!commentNickName.equals(nickName)) {
            return "redirect:/boards/{id}";
        }

        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-18  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }

        updateForm.setId(commentId);
        commentService.updateComment(updateForm);
        return "redirect:/boards/{id}";
    }


    @GetMapping("/boards/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @PathVariable("id") Long id,
                                @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        //본인 댓글만 삭제 가능
        String commentNickName = commentService.readComment(commentId).getNickName();
        if (!commentNickName.equals(nickName)) {
            return "redirect:/boards/{id}";
        }

        commentService.deleteComment(commentId);
        return "redirect:/boards/{id}";
    }
}
