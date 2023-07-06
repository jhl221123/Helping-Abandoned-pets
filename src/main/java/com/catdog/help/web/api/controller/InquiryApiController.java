package com.catdog.help.web.api.controller;

import com.catdog.help.exception.NotAuthorizedException;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.web.api.request.inquiry.EditInquiryRequest;
import com.catdog.help.web.api.request.inquiry.SaveInquiryRequest;
import com.catdog.help.web.api.response.inquiry.PageInquiryResponse;
import com.catdog.help.web.api.response.inquiry.ReadInquiryResponse;
import com.catdog.help.web.api.response.inquiry.SaveInquiryResponse;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import com.catdog.help.web.form.search.InquirySearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.catdog.help.MyConst.INQUIRY;
import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryApiController {

    private final InquiryService inquiryService;
    private final BoardService boardService;


    @PostMapping("/new")
    public SaveInquiryResponse saveBoard(@SessionAttribute(LOGIN_USER) String nickname, @RequestBody SaveInquiryRequest request) {
        Long savedId = inquiryService.save(new SaveInquiryForm(request), nickname);
        return new SaveInquiryResponse(savedId);
    }

    @GetMapping
    public Page<PageInquiryResponse> readPage(@RequestBody InquirySearch search,
                                              @PageableDefault(size = 5, direction = DESC) Pageable pageable) {
        return inquiryService.search(search, pageable).map(PageInquiryResponse::new);
    }

    @GetMapping("/{id}")
    public ReadInquiryResponse readBoard(@PathVariable Long id) {
        ReadInquiryForm form = inquiryService.read(id);
        return new ReadInquiryResponse(form);
    }

    @PostMapping("/{id}/edit")
    public void editBoard(@PathVariable(value = "id") Long boardId, @SessionAttribute(LOGIN_USER) String nickname,
                          @RequestBody EditInquiryRequest request) {
        if (!isWriter(boardId, nickname)) {
            throw new NotAuthorizedException(INQUIRY);
        }
        inquiryService.update(new EditInquiryForm(request));
    }

    @PostMapping("/{id}/delete")
    public void deleteBoard(@PathVariable(value = "id") Long boardId, @SessionAttribute(LOGIN_USER) String nickname) {
        if (!isWriter(boardId, nickname)) {
            throw new NotAuthorizedException(INQUIRY);
        }
        inquiryService.delete(boardId);
    }

    private Boolean isWriter(Long boardId, String nickname) {
        String writer = boardService.getWriter(boardId);
        return writer.equals(nickname) ? true : false;
    }
}
