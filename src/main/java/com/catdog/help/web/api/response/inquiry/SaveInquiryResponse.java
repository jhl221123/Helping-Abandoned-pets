package com.catdog.help.web.api.response.inquiry;

import lombok.Getter;

@Getter
public class SaveInquiryResponse {

    private Long id;

    public SaveInquiryResponse(Long id) {
        this.id = id;
    }
}
