package com.catdog.help.web.controller;

import com.catdog.help.service.CommentService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.catdog.help.web.SessionConst.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/inquiries/new")
    public String createBoardForm(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        SaveInquiryForm saveForm = new SaveInquiryForm();
        saveForm.setNickname(nickname);
        model.addAttribute("saveForm", saveForm);
        return "inquiries/create";
    }

    @PostMapping("/inquiries/new")
    public String createBoard(@Validated @ModelAttribute("saveForm") SaveInquiryForm saveForm,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "inquiries/create";
        }
        inquiryService.saveBoard(saveForm);
        return "redirect:/inquiries?page=1";
    }

    @GetMapping("/inquiries")
    public String readPage(@RequestParam("page") int page, Model model,
                           @SessionAttribute(name = LOGIN_USER) String nickname) {

        Boolean isManager = userService.isManager(nickname);
        model.addAttribute("isManager", isManager);

        List<PageInquiryForm> pageForms = inquiryService.readPage(page);
        model.addAttribute("pageForms", pageForms);

        int lastPage = inquiryService.countPage();
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("nickname", nickname);
        return "inquiries/list";
    }

    @GetMapping("/inquiries/{id}")
    public String readBoard(@PathVariable("id") Long id, Model model,
                            @SessionAttribute(name = LOGIN_USER) String nickname,
                            @ModelAttribute("updateCommentForm") UpdateCommentForm updateCommentForm,
                            @RequestParam(value = "clickReply", required = false) Long parentCommentId) {
        ReadInquiryForm readForm = inquiryService.readBoard(id);
        model.addAttribute("readForm", readForm);
        model.addAttribute("nickname", nickname);

        // TODO: 2023-04-13 매니저, 작성자 제외 차단

        List<CommentForm> commentForms = commentService.readComments(id);
        if (commentForms != null) {
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
    public String editBoardForm(@PathVariable("id") Long id, Model model,
                                @SessionAttribute(name = LOGIN_USER) String nickname) {
        EditInquiryForm editForm = inquiryService.getEditForm(id);

        //작성자 외 접근제한
        if (!editForm.getNickname().equals(nickname)) {
            return "redirect:/inquiries/{id}";
        }

        model.addAttribute("editForm", editForm);
        return "inquiries/edit";
    }

    @PostMapping("/inquiries/{id}/edit")
    public String editBoard(@Validated @ModelAttribute("editForm") EditInquiryForm editForm, BindingResult bindingResult,
                            @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 외 접근제한
        if (!editForm.getNickname().equals(nickname)) {
            return "redirect:/inquiries/{id}";
        }

        if (bindingResult.hasErrors()) {
            return "inquiries/edit";
        }

        inquiryService.updateBoard(editForm);
        return "redirect:/inquiries/{id}";
    }

    @GetMapping("/inquiries/{id}/delete")
    public String deleteBoardForm(@PathVariable("id") Long id, Model model,
                                  @SessionAttribute(name = LOGIN_USER) String nickname) {

        ReadInquiryForm readForm = inquiryService.readBoard(id);
        //작성자 외 접근제한
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/inquiries/{id}";
        }

        model.addAttribute("readForm", readForm);
        return "inquiries/delete";
    }

    @PostMapping("/inquiries/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 외 접근제한
        if (!inquiryService.readBoard(id).getNickname().equals(nickname)) {
            return "redirect:/inquiries/{id}";
        }

        inquiryService.deleteBoard(id);
        return "redirect:/inquiries?page=1";
    }
}
