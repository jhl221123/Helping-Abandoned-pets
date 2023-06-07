package com.catdog.help.web.form.search;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ItemSearch {

    private String region;

    private String itemName;


    @Builder
    public ItemSearch(String region, String itemName) {
        this.region = region;
        this.itemName = itemName;
    }
}
