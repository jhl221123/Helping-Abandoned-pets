package com.catdog.help.web.api.response.item;

import com.catdog.help.web.form.item.PageItemForm;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageItemResponse {

    private List<PageItemForm> content;
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;


    @Builder
    public PageItemResponse(List<PageItemForm> content, int page, int size, Long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
