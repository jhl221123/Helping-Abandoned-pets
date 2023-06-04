package com.catdog.help.web.api.response.lost;

import com.catdog.help.web.form.lost.PageLostForm;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PageLostResponse {

    private List<PageLostForm> content = new ArrayList<>();
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;


    @Builder
    public PageLostResponse(List<PageLostForm> content, int page, int size, Long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
