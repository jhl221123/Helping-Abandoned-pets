package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    @PostMapping("/comments/parent")
    public String createParentComment(@RequestParam("id") Long boardId, RedirectAttributes redirectAttributes,
                                      @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                                      @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult) {
        redirectAttributes.addAttribute("id", boardId);

        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-17  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }
        commentForm.setBoardId(boardId);
        commentForm.setNickname(nickname);
        commentService.createComment(commentForm, -1L);

        return targetBoard(boardId);
    }

    @GetMapping("/comments/child")
    public String createChildCommentForm(@RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes,
                                         @RequestParam("clickReply") Long parentId) {
        redirectAttributes.addAttribute("id", boardId);

        redirectAttributes.addAttribute("clickReply", parentId);
        return targetBoard(boardId);
    }

    @PostMapping("/comments/child")
    public String createChildComment(@RequestParam("id") Long boardId, RedirectAttributes redirectAttributes, @RequestParam("parentId") Long parentId,
                                     @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                                     @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult) {
        redirectAttributes.addAttribute("id", boardId);

        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-17  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }
        commentForm.setBoardId(boardId);
        commentForm.setNickname(nickname);
        commentService.createComment(commentForm, parentId);

        return targetBoard(boardId);
    }

    @GetMapping("/comments/{id}/edit")
    public String editCommentForm(@PathVariable("id") Long id, @RequestParam("boardId") Long boardId,
                                  @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("id", boardId);

        // TODO: 2023-04-12 댓글 작성자 외 리턴

        UpdateCommentForm updateCommentForm = commentService.getUpdateCommentForm(id, nickname);
        redirectAttributes.addAttribute("updateCommentId", id);
        redirectAttributes.addFlashAttribute("updateCommentForm", updateCommentForm);

        return targetBoard(boardId);
    }

    @PostMapping("/comments/{id}/edit")
    public String editComment(@PathVariable("id") Long id, @RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes,
                                @Validated @ModelAttribute("updateCommentForm") UpdateCommentForm updateForm, BindingResult bindingResult,
                                @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname) {
        redirectAttributes.addAttribute("id", boardId);

        //본인 댓글만 수정 가능
        String commentNickname = commentService.readComment(id).getNickname();
        if (!commentNickname.equals(nickname)) {
            return "redirect:/boards/{id}";
        }

        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-18  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }

        updateForm.setCommentId(id);
        commentService.updateComment(updateForm);

        return targetBoard(boardId);
    }


    @GetMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable("id") Long id, @RequestParam("boardId") Long boardId,
                                RedirectAttributes redirectAttributes,
                                @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname) {
        redirectAttributes.addAttribute("id", boardId);

        //본인 댓글만 삭제 가능
        String commentNickname = commentService.readComment(id).getNickname();
        if (!commentNickname.equals(nickname)) {
            return "redirect:/boards/{id}";
        }

        commentService.deleteComment(id);

        return targetBoard(boardId);
    }

    private String targetBoard(Long boardId) {
        if (boardService.isBulletin(boardId)) {
            return "redirect:/boards/{id}";
        }
        return "redirect:/inquiries/{id}";
    }
}
