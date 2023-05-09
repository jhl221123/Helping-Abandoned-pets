package com.catdog.help.web.form.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SaveItemForm {

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank
    @Length(max = 250)
    private String content;

    @NotBlank
    @Length(max = 13)
    private String itemName;

    @NotNull
    @Max(10000)
    @PositiveOrZero
    private int price;

    @NotBlank
    private String region;

    private List<MultipartFile> images = new ArrayList<>();


    public SaveItemForm() {
    }

    @Builder
    public SaveItemForm(String title, String content, String itemName, int price, String region, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.itemName = itemName;
        this.price = price;
        this.region = region;
        this.images = images;
    }
}
