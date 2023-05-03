package com.catdog.help.web.form.search;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class InquirySearch {

    private String title;


    @Builder
    public InquirySearch(String title) {
        this.title = title;
    }
}
