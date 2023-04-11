package com.catdog.help.web.controller;

import com.catdog.help.service.InquiryService;
import com.catdog.help.service.UserService;
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
        List<PageInquiryForm> pageForms = inquiryService.readPage(page);
        model.addAttribute("pageForms", pageForms);

        int lastPage = inquiryService.countPage();
        model.addAttribute("lastPage", lastPage);

        model.addAttribute("nickname", nickname);
        return "inquiries/list";
    }

    @GetMapping("/inquiries/{id}")
    public String readBoard(@PathVariable("id") Long id, Model model,
                            @SessionAttribute(name = LOGIN_USER) String nickname) {
        ReadInquiryForm readForm = inquiryService.readBoard(id);
        model.addAttribute("readForm", readForm);
        model.addAttribute("nickname", nickname);
        return "inquiries/detail";
    }
}
