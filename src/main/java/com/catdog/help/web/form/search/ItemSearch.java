package com.catdog.help.web.form.search;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ItemSearch {

    private String title;

    private String itemName;


    @Builder
    public ItemSearch(String title, String itemName) {
        this.title = title;
        this.itemName = itemName;
    }
}
