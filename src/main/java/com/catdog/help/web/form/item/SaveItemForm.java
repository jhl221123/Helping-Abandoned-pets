package com.catdog.help.web.form.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SaveItemForm {

    @NotBlank
    @Length(max = 25)
    private String title;

    @NotBlank
    @Length(max = 250)
    private String content;

    @NotBlank
    @Length(max = 15)
    private String itemName;

    @NotNull
    @Max(10000)
    private int price;

    private List<MultipartFile> images = new ArrayList<>();


    public SaveItemForm() {
    }

    @Builder
    public SaveItemForm(String title, String content, String itemName, int price, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.itemName = itemName;
        this.price = price;
        this.images = images;
    }
}
