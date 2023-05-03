package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import com.catdog.help.web.form.search.InquirySearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String getSaveForm(Model model) {
        SaveInquiryForm saveForm = new SaveInquiryForm();
        model.addAttribute("saveForm", saveForm);
        return "inquiries/create";
    }

    @PostMapping("/inquiries/new")
    public String saveBoard(@SessionAttribute(name = LOGIN_USER) String nickname,
                            @Validated @ModelAttribute("saveForm") SaveInquiryForm saveForm,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "inquiries/create";
        }
        Long boardId = inquiryService.save(saveForm, nickname);
        redirectAttributes.addAttribute("boardId", boardId);
        return "redirect:/inquiries/{boardId}";
    }

    @GetMapping("/inquiries")
    public String getPage(@ModelAttribute InquirySearch inquirySearch, @SessionAttribute(name = LOGIN_USER) String nickname,
                          @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Model model) {

        Boolean isManager = userService.isManager(nickname);
        model.addAttribute("isManager", isManager);

        Page<PageInquiryForm> pageForms = inquiryService.search(inquirySearch, pageable);
        model.addAttribute("pageForms", pageForms);

        int offset = pageable.getPageNumber() / 5 * 5;
        model.addAttribute("offset", offset);

        int limit = offset + 4;
        int endPage = Math.max(pageForms.getTotalPages() - 1, 0);
        int lastPage = getLastPage(limit, endPage);
        model.addAttribute("lastPage", lastPage);

        boolean isEnd = false;
        if (lastPage == endPage) {
            isEnd = true;
        }
        model.addAttribute("isEnd", isEnd);

        return "inquiries/list";
    }

    @GetMapping("/inquiries/{id}")
    public String readBoard(@PathVariable("id") Long id, Model model,
                            @SessionAttribute(name = LOGIN_USER) String nickname) {
        // TODO: 2023-04-26 bindingResult 이용해서 뷰템플릿에 오류 보이도록 만들자.

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
    public String editBoard(@SessionAttribute(name = LOGIN_USER) String nickname,
                            @Validated @ModelAttribute("editForm") EditInquiryForm editForm, BindingResult bindingResult) {
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


    private int getLastPage(int limit, int endPage) {
        int lastPage = Math.min(endPage, limit);
        if(lastPage<0) lastPage = 0;
        return lastPage;
    }

    private Boolean isWriter(Long id, String nickname) {
        String writer = boardService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }
}
