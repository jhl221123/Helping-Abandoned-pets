package com.catdog.help.web.form.item;

import com.catdog.help.domain.board.Item;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EditItemForm {

    @NotNull
    private Long id;

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

    private ReadImageForm oldLeadImage;

    private MultipartFile newLeadImage;

    private List<ReadImageForm> oldImages = new ArrayList<>();

    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Long> deleteImageIds = new ArrayList<>();


    public EditItemForm() { //컨트롤러 파라미터 바인딩
    }

    @Builder
    public EditItemForm(Item findBoard, List<ReadImageForm> readForms) {
        this.id = findBoard.getId();
        this.title = findBoard.getTitle();
        this.content = findBoard.getContent();
        this.itemName = findBoard.getItemName();
        this.price = findBoard.getPrice();

        //대표이미지
        this.oldLeadImage = readForms.get(0);
        for (int i = 1; i < readForms.size(); i++) {
            //대표이미지 제외
            this.getOldImages().add(readForms.get(i));
        }
    }
}
