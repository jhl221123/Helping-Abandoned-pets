package com.catdog.help.web.api.response.bulletin;

import com.catdog.help.web.form.bulletin.PageBulletinForm;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageBulletinResponse {

    private List<PageBulletinForm> content;
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;


    @Builder
    public PageBulletinResponse(List<PageBulletinForm> content, int page, int size, Long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
