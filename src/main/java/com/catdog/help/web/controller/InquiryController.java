package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.EditCommentForm;
import com.catdog.help.web.form.inquiry.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final CommentService commentService;
    private final UserService userService;
    private final BoardService boardService;

    @GetMapping("/inquiries/new")
    public String getSaveForm(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        SaveInquiryForm saveForm = new SaveInquiryForm();
        saveForm.setNickname(nickname);
        model.addAttribute("saveForm", saveForm);
        return "inquiries/create";
    }

    @PostMapping("/inquiries/new")
    public String saveBoard(@Validated @ModelAttribute("saveForm") SaveInquiryForm saveForm,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "inquiries/create";
        }
        Long boardId = inquiryService.save(saveForm);
        model.addAttribute("boardId", boardId);
        return "redirect:/inquiries/{boardId}";
    }

    @GetMapping("/inquiries")
    public String getPage(@SessionAttribute(name = LOGIN_USER) String nickname,
                          Pageable pageable, Model model) {

        Boolean isManager = userService.isManager(nickname);
        model.addAttribute("isManager", isManager);

        Page<PageInquiryForm> pageForms = inquiryService.getPage(pageable);
        model.addAttribute("pageForms", pageForms);

        int lastPage = pageForms.getTotalPages();
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("nickname", nickname);
        return "inquiries/list";
    }

    @GetMapping("/inquiries/{id}")
    public String readBoard(@PathVariable("id") Long id, Model model,
                            @SessionAttribute(name = LOGIN_USER) String nickname,
                            @ModelAttribute("editCommentForm") EditCommentForm editCommentForm,
                            @RequestParam(value = "clickReply", required = false) Long parentCommentId) {
        ReadInquiryForm readForm = inquiryService.read(id);
        model.addAttribute("readForm", readForm);
        model.addAttribute("nickname", nickname);

        // TODO: 2023-04-13 매니저, 작성자 제외 차단

        List<CommentForm> commentForms = commentService.readByBoardId(id);
        if (!commentForms.isEmpty()) {
            model.addAttribute("commentForms", commentForms);
        }

        //댓글
        CommentForm commentForm = new CommentForm();
        model.addAttribute("commentForm", commentForm);

        //답글
        model.addAttribute("clickReply", parentCommentId);

        return "inquiries/detail";
    }

    @GetMapping("/inquiries/{id}/edit")
    public String getEditForm(@PathVariable("id") Long id, Model model,
                              @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        EditInquiryForm editForm = inquiryService.getEditForm(id);
        model.addAttribute("editForm", editForm);
        return "inquiries/edit";
    }

    @PostMapping("/inquiries/{id}/edit")
    public String editBoard(@Validated @ModelAttribute("editForm") EditInquiryForm editForm, BindingResult bindingResult,
                            @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(editForm.getId(), nickname)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "inquiries/edit";
        }

        inquiryService.update(editForm);
        return "redirect:/inquiries/{id}";
    }

    @GetMapping("/inquiries/{id}/delete")
    public String getDeleteForm(@PathVariable("id") Long id, Model model,
                                  @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        ReadInquiryForm readForm = inquiryService.read(id);
        model.addAttribute("readForm", readForm);
        return "inquiries/delete";
    }

    @PostMapping("/inquiries/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        inquiryService.delete(id);
        return "redirect:/inquiries?page=0";
    }


    private Boolean isWriter(Long id, String nickname) {
        String writer = boardService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }
}
