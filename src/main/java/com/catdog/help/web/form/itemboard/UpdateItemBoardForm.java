package com.catdog.help.web.form.itemboard;

import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
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
public class UpdateItemBoardForm {

    @NotNull
    private Long id;

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

    private ReadUploadFileForm oldLeadImage;

    private MultipartFile newLeadImage;

    private List<ReadUploadFileForm> oldImages = new ArrayList<>();

    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Integer> deleteImageIds = new ArrayList<>();

    public UpdateItemBoardForm() { //컨트롤러 파라미터 바인딩
    }

    public UpdateItemBoardForm(ItemBoard findBoard, List<ReadUploadFileForm> readForms) {
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
