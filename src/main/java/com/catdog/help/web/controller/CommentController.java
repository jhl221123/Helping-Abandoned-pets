package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.EditCommentForm;
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
    public String saveParent(@RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes,
                             @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                             @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult) {
        redirectAttributes.addAttribute("id", boardId);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
            return redirectBoard(boardId);
        }
        commentForm.setBoardId(boardId);
        commentForm.setNickname(nickname);
        commentService.save(commentForm, -1L);

        return redirectBoard(boardId);
    }

    @GetMapping("/comments/child")
    public String getChildForm(@RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes,
                               @RequestParam("clickReply") Long parentId) {
        redirectAttributes.addAttribute("id", boardId);
        log.info("clickReply={}", parentId);

        redirectAttributes.addFlashAttribute("clickReply", parentId);
        return redirectBoard(boardId);
    }

    @PostMapping("/comments/child")
    public String saveChild(@RequestParam("boardId") Long boardId, @RequestParam("parentId") Long parentId,
                            @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname, RedirectAttributes redirectAttributes,
                            @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult) {
        redirectAttributes.addAttribute("id", boardId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
            return redirectBoard(boardId);
        }

        commentForm.setBoardId(boardId);
        commentForm.setNickname(nickname);
        commentService.save(commentForm, parentId);
        return redirectBoard(boardId);
    }

    @GetMapping("/comments/{id}/edit")
    public String getEditForm(@PathVariable("id") Long id, @RequestParam("boardId") Long boardId,
                              @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                              RedirectAttributes redirectAttributes) {
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        EditCommentForm editCommentForm = commentService.getEditForm(id, nickname);
        redirectAttributes.addFlashAttribute("editCommentForm", editCommentForm);
        redirectAttributes.addAttribute("id", boardId);

        return redirectBoard(boardId);
    }

    @PostMapping("/comments/{id}/edit")
    public String edit(@PathVariable("id") Long id, @RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes,
                       @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                       @Validated @ModelAttribute("editCommentForm") EditCommentForm editForm, BindingResult bindingResult) {
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        redirectAttributes.addAttribute("id", boardId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
            return redirectBoard(boardId);
        }

        editForm.setCommentId(id);
        commentService.update(editForm);
        return redirectBoard(boardId);
    }


    @GetMapping("/comments/{id}/delete")
    public String delete(@PathVariable("id") Long id, @RequestParam("boardId") Long boardId,
                         @SessionAttribute(name = SessionConst.LOGIN_USER) String nickname,
                         RedirectAttributes redirectAttributes) {
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        redirectAttributes.addAttribute("id", boardId);
        commentService.delete(id);
        return redirectBoard(boardId);
    }


    private Boolean isWriter(Long id, String nickname) {
        String writer = commentService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }

    private String redirectBoard(Long boardId) {
        if (boardService.isBulletin(boardId)) {
            return "redirect:/bulletins/{id}";
        } else if (boardService.isLost(boardId)) {
            return "redirect:/lost/{id}";
        } else {
            return "redirect:/inquiries/{id}";
        }
    }
}
