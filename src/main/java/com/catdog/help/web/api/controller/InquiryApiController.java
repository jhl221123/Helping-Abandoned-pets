package com.catdog.help.web.api.controller;

import com.catdog.help.service.InquiryService;
import com.catdog.help.web.api.request.inquiry.SaveInquiryRequest;
import com.catdog.help.web.api.response.inquiry.ReadInquiryResponse;
import com.catdog.help.web.api.response.inquiry.SaveInquiryResponse;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryApiController {

    private final InquiryService inquiryService;


    @PostMapping("/new")
    public SaveInquiryResponse saveBoard(@SessionAttribute(LOGIN_USER) String nickname, @RequestBody SaveInquiryRequest request) {
        Long savedId = inquiryService.save(new SaveInquiryForm(request), nickname);
        return new SaveInquiryResponse(savedId);
    }

    @GetMapping("/{id}")
    public ReadInquiryResponse readBoard(@PathVariable Long id) {
        ReadInquiryForm form = inquiryService.read(id);
        return new ReadInquiryResponse(form);
    }
}
