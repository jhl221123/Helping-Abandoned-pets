package com.catdog.help.web.api.response.inquiry;

import com.catdog.help.web.form.inquiry.PageInquiryForm;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageInquiryResponse {

    private List<PageInquiryForm> content;
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;


    @Builder
    public PageInquiryResponse(List<PageInquiryForm> content, int page, int size, Long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
