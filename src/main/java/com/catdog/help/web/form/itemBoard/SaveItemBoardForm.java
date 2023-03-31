package com.catdog.help.web.form.itemBoard;

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
public class SaveItemBoardForm {

    @NotBlank
    @Length(max = 15)
    private String itemName;

    @NotNull
    @Max(10000)
    private int price;

    @NotBlank
    @Length(max = 25)
    private String title;

    @NotBlank
    @Length(max = 250)
    private String content;

    private List<MultipartFile> images = new ArrayList<>();
}
